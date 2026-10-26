package org.taack.cad.intellij;

import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CadHighlightTest {
    private static CadFeature feature(String id, String kind, CadFeature... children) {
        return new CadFeature(id, kind, id, null, null, 0, false, false, false, null, List.of(children));
    }

    private static CadFeature place(String id, String placed) {
        return new CadFeature(id, "place", id, null, null, 0, false, false, false, placed, List.of());
    }

    private static CadFeature suppressed(String id, String kind, @Nullable String placed) {
        return new CadFeature(id, kind, id, null, null, 0, false, false, true, placed, List.of());
    }

    // rail: a part screw declared and placed by a method, a sub-assembly jack, the screw again
    private static final List<CadFeature> RAIL = List.of(
            feature("rail", "assembly",
                    feature("rail/group:screws", "group",
                            feature("rail/group:screws/part:screw", "part",
                                    feature("rail/group:screws/part:screw/cylinder0", "cylinder"),
                                    feature("rail/group:screws/part:screw/group:head", "group",
                                            feature("rail/group:screws/part:screw/group:head/cone0", "cone"))),
                            place("rail/group:screws/place0", "rail/group:screws/part:screw")),
                    feature("rail/assembly:jack", "assembly",
                            feature("rail/assembly:jack/part:contact", "part"),
                            place("rail/assembly:jack/place0", "rail/assembly:jack/part:contact"),
                            place("rail/assembly:jack/place1", "rail/assembly:jack/part:contact")),
                    place("rail/place0", "rail/assembly:jack"),
                    place("rail/place1", "rail/group:screws/part:screw")),
            feature("solid0", "solid", feature("solid0/box0", "box")));

    @Test
    public void theNodesAreNumberedAsTheRunnerWritesThem() {
        CadHighlight rail = new CadHighlight(RAIL, "rail");
        // rail 0, screw 1, jack 2, its contacts 3 and 4, the screw again 5
        assertEquals(List.of(0), rail.nodes("rail"));
        assertEquals(List.of(1, 5), rail.nodes("rail/group:screws/part:screw"));
        assertEquals(List.of(2), rail.nodes("rail/place0"));
        assertEquals(List.of(3, 4), rail.nodes("rail/assembly:jack/part:contact"));
        assertEquals(List.of(4), rail.nodes("rail/assembly:jack/place1"));
    }

    @Test
    public void aFeatureIsShownByTheNodesOfItsPart() {
        CadHighlight rail = new CadHighlight(RAIL, "rail");
        assertEquals(List.of(1, 5), rail.nodes("rail/group:screws/part:screw/cylinder0"));
        // a group in a part too, a group of an assembly by its features
        assertEquals(List.of(1, 5), rail.nodes("rail/group:screws/part:screw/group:head"));
        assertEquals(List.of(1, 5), rail.nodes("rail/group:screws"));
    }

    @Test
    public void aFeatureOutOfTheModelHasNoNodes() {
        assertEquals(List.of(), new CadHighlight(RAIL, "rail").nodes("solid0/box0"));
        assertEquals(List.of(0), new CadHighlight(RAIL, "solid0").nodes("solid0/box0"));
        assertEquals(List.of(), new CadHighlight(RAIL, null).nodes("rail"));
    }

    @Test
    public void thePlacementsAfterTheStopOfTheRunHaveNoNodes() {
        CadHighlight stopped = new CadHighlight(List.of(feature("rail", "assembly",
                feature("rail/part:screw", "part", feature("rail/part:screw/cylinder0", "cylinder")),
                place("rail/place0", "rail/part:screw"),
                suppressed("rail/place1", "place", "rail/part:screw"),
                place("rail/place2", "rail/part:screw"))), "rail");
        assertEquals(List.of(1, 2), stopped.nodes("rail/part:screw"));
        assertEquals(List.of(), stopped.nodes("rail/place1"));
        assertEquals(List.of(2), stopped.nodes("rail/place2"));
    }

    @Test
    public void onlyTheOperationsDoneHaveShapes() {
        assertFalse(new CadHighlight(List.of(feature("solid0", "solid", suppressed("solid0/box0", "box", null))), "solid0").hasShape("solid0/box0"));
    }

    @Test
    public void onlyTheOperationsHaveShapes() {
        CadHighlight rail = new CadHighlight(RAIL, "rail");
        assertTrue(rail.hasShape("rail/group:screws/part:screw/cylinder0"));
        assertFalse(rail.hasShape("rail/group:screws/part:screw"));
        assertFalse(rail.hasShape("rail/place0"));
    }
}
