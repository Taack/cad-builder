import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';

const theme = new URLSearchParams(location.search).get('theme') === 'dark' ? 'dark' : 'light';
document.documentElement.dataset.theme = theme;
const message = document.getElementById('message');
const animationBar = document.getElementById('animation');
const playButton = document.getElementById('play');
const timeSlider = document.getElementById('time');

window.addEventListener('error', event => showMessage(event.message));

const renderer = new THREE.WebGLRenderer({ antialias: true });
renderer.setPixelRatio(window.devicePixelRatio);
renderer.setClearColor(getComputedStyle(document.documentElement).getPropertyValue('--background').trim());
document.body.prepend(renderer.domElement);

const scene = new THREE.Scene();
scene.add(new THREE.HemisphereLight(0xffffff, 0x8d8d8d, 1.5));
const camera = new THREE.PerspectiveCamera(45, 1, 0.001, 1000);
// a light that follows the camera, so that the faces turned to the viewer are lit
const headlight = new THREE.DirectionalLight(0xffffff, 2);
headlight.position.set(0.5, 1, 1);
camera.add(headlight);
scene.add(camera);
const controls = new OrbitControls(camera, renderer.domElement);

const loader = new GLTFLoader();
const timer = new THREE.Timer();
let model = null;
let mixer = null;
let duration = 0;
let playing = true;
let edgesVisible = true;
let loading = 0;

function showMessage(text) {
    message.textContent = text;
    message.style.display = text ? 'flex' : 'none';
}

function resize() {
    renderer.setSize(window.innerWidth, window.innerHeight);
    camera.aspect = window.innerWidth / Math.max(window.innerHeight, 1);
    camera.updateProjectionMatrix();
}

function prepare(root) {
    root.traverse(object => {
        // the faces are pushed back, so that their boundaries are drawn over them
        if (object.isMesh) {
            object.material.polygonOffset = true;
            object.material.polygonOffsetFactor = 1;
            object.material.polygonOffsetUnits = 1;
        }
        if (object.isLineSegments || object.isLine) object.visible = edgesVisible;
    });
}

function dispose(root) {
    scene.remove(root);
    root.traverse(object => {
        object.geometry?.dispose();
        object.material?.dispose();
    });
}

function fit() {
    if (!model) return;
    const box = new THREE.Box3().setFromObject(model);
    if (box.isEmpty()) return;
    const center = box.getCenter(new THREE.Vector3());
    const radius = box.getSize(new THREE.Vector3()).length() / 2;
    const distance = radius / Math.sin(THREE.MathUtils.degToRad(camera.fov / 2));
    const direction = new THREE.Vector3(1, 0.8, 1.2).normalize();
    camera.position.copy(center).addScaledVector(direction, distance);
    camera.near = distance / 100;
    camera.far = distance * 100;
    camera.updateProjectionMatrix();
    controls.target.copy(center);
    controls.update();
}

function setAnimation(gltf) {
    mixer = null;
    duration = 0;
    if (gltf.animations.length) {
        mixer = new THREE.AnimationMixer(gltf.scene);
        gltf.animations.forEach(clip => {
            mixer.clipAction(clip).play();
            duration = Math.max(duration, clip.duration);
        });
    }
    timeSlider.max = String(duration);
    animationBar.style.display = mixer ? 'flex' : 'none';
}

window.cadPreview = {
    show(url, refit) {
        const request = ++loading;
        loader.load(url, gltf => {
            // a slower load of an older model is dropped
            if (request !== loading) return;
            if (model) dispose(model);
            model = gltf.scene;
            prepare(model);
            scene.add(model);
            setAnimation(gltf);
            showMessage('');
            if (refit) fit();
        }, undefined, error => showMessage('Cannot read the model: ' + (error.message || error)));
    },
    fit,
    edges(visible) {
        edgesVisible = visible;
        if (model) prepare(model);
    },
};

playButton.addEventListener('click', () => {
    playing = !playing;
    playButton.textContent = playing ? '⏸' : '▶';
});
timeSlider.addEventListener('input', () => {
    if (mixer) mixer.setTime(Number(timeSlider.value));
});

window.addEventListener('resize', resize);
resize();
renderer.setAnimationLoop(timestamp => {
    timer.update(timestamp);
    const delta = timer.getDelta();
    if (mixer && playing) {
        mixer.update(delta);
        timeSlider.value = String(mixer.time % duration);
    }
    renderer.render(scene, camera);
});
