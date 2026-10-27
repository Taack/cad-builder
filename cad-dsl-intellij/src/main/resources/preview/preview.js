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
// the glTF node objects of the model, by index; the nodes of the selected feature, and the shapes shown over them
let nodeObjects = [];
let objectNodes = new Map();
let highlighted = [];
let shapeUrl = null;
let shapes = [];
let shapeLoading = 0;
const style = getComputedStyle(document.documentElement);
const highlightColor = new THREE.Color(style.getPropertyValue('--highlight').trim());
const shapeColor = new THREE.Color(style.getPropertyValue('--shape').trim());

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
    root.removeFromParent();
    root.traverse(object => {
        object.geometry?.dispose();
        object.material?.dispose();
        object.userData.original?.dispose();
    });
}

// The glTF nodes under the pointer, the deepest first, to the IDE that opens the line that made them; none when it
// points at nothing
const raycaster = new THREE.Raycaster();
function pick(event) {
    if (!model || !window.cadPick) return;
    const rect = renderer.domElement.getBoundingClientRect();
    const pointer = new THREE.Vector2((event.clientX - rect.left) / rect.width * 2 - 1,
        -(event.clientY - rect.top) / rect.height * 2 + 1);
    raycaster.setFromCamera(pointer, camera);
    raycaster.params.Line.threshold = camera.position.distanceTo(controls.target) / 200;
    const hits = raycaster.intersectObject(model, true).filter(it => it.object.visible && !it.object.userData.shape);
    // a face rather than the boundary drawn over it; the edges of a shape without faces
    const hit = hits.find(it => it.object.isMesh) ?? hits[0];
    const nodes = [];
    for (let o = hit?.object; o; o = o.parent) if (objectNodes.has(o)) nodes.push(objectNodes.get(o));
    window.cadPick(JSON.stringify(nodes));
}

// Puts back the materials the highlight replaced
function restoreMaterials() {
    model?.traverse(object => {
        if (!object.userData.original) return;
        object.material.dispose();
        object.material = object.userData.original;
        delete object.userData.original;
    });
}

// The parts of the selected feature tinted, the others faded so that they show through
function applyHighlight() {
    restoreMaterials();
    if (!model || !highlighted.length) return;
    const selected = new Set(highlighted.map(i => nodeObjects[i]).filter(Boolean));
    model.traverse(object => {
        if (!object.isMesh && !object.isLineSegments && !object.isLine || object.userData.shape) return;
        let inside = false;
        for (let o = object; o && !inside; o = o.parent) inside = selected.has(o);
        const material = object.material.clone();
        object.userData.original = object.material;
        if (inside && object.isMesh) {
            material.emissive.copy(highlightColor);
            material.emissiveIntensity = 0.35;
        } else if (!inside) {
            material.transparent = true;
            material.opacity = 0.15;
            material.depthWrite = false;
        }
        object.material = material;
    });
}

// The shapes of the feature, drawn over its parts and through them, in each place of its part
function applyShapes(gltf) {
    shapes.forEach(dispose);
    shapes = [];
    if (!gltf || !model) return;
    // the shapes are in the coordinates of the part: their root node, which turns them to glTF units, is dropped
    const content = gltf.scene.children[0];
    content.traverse(object => {
        if (object.isMesh) object.material = new THREE.MeshStandardMaterial({ color: shapeColor, transparent: true,
            opacity: 0.45, depthTest: false, depthWrite: false, side: THREE.DoubleSide });
        if (object.isLineSegments || object.isLine) object.material = new THREE.LineBasicMaterial({ color: shapeColor,
            depthTest: false });
        object.renderOrder = 1;
        object.userData.shape = true;
    });
    for (const i of highlighted) {
        const node = nodeObjects[i];
        if (!node) continue;
        const shape = content.clone();
        shape.position.set(0, 0, 0);
        shape.quaternion.identity();
        shape.scale.set(1, 1, 1);
        node.add(shape);
        shapes.push(shape);
    }
}

function loadShapes() {
    const request = ++shapeLoading;
    if (!shapeUrl) {
        applyShapes(null);
        return;
    }
    loader.load(shapeUrl, gltf => {
        if (request === shapeLoading) applyShapes(gltf);
    }, undefined, error => console.warn('Cannot read the shapes: ' + (error.message || error)));
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
            shapes = [];
            model = gltf.scene;
            nodeObjects = [];
            objectNodes = new Map();
            gltf.parser.associations.forEach((value, object) => {
                if (value?.nodes === undefined) return;
                nodeObjects[value.nodes] = object;
                objectNodes.set(object, value.nodes);
            });
            prepare(model);
            scene.add(model);
            applyHighlight();
            loadShapes();
            setAnimation(gltf);
            showMessage('');
            if (refit) fit();
        }, undefined, error => showMessage('Cannot read the model: ' + (error.message || error)));
    },
    fit,
    /** Highlights the parts of the glTF nodes, none for an empty list */
    highlight(nodes) {
        highlighted = nodes;
        applyHighlight();
    },
    /** Shows the shapes of the glTF file at url over the highlighted parts, none for null */
    overlay(url) {
        shapeUrl = url;
        loadShapes();
    },
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

// a click picks, a drag turns the view
let pressed = null;
renderer.domElement.addEventListener('pointerdown', event => {
    pressed = event.button === 0 ? { x: event.clientX, y: event.clientY } : null;
});
renderer.domElement.addEventListener('pointerup', event => {
    if (pressed && Math.hypot(event.clientX - pressed.x, event.clientY - pressed.y) < 4) pick(event);
    pressed = null;
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
