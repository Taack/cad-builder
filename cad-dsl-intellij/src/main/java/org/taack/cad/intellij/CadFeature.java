package org.taack.cad.intellij;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * An operation of a script, in the tree of features the runner sends: CadFeatureRecorder.tree() of cad-dsl
 *
 * @param className Class of the code that called it, a script, a class beside it or a class of a jar of parts
 * @param file      Name of the file of that code, without its directory
 * @param line      Line of the call, 0 when unknown
 * @param detail    Sketch element: an edge, an arc, a text...
 * @param error     Last operation of a failed run
 */
public record CadFeature(String id, String kind, String label,
                         @Nullable String className, @Nullable String file, int line,
                         boolean detail, boolean error, List<CadFeature> children) {

    static List<CadFeature> read(@Nullable JsonArray features) {
        List<CadFeature> read = new ArrayList<>();
        if (features != null) features.forEach(it -> read.add(read(it.getAsJsonObject())));
        return read;
    }

    private static CadFeature read(JsonObject feature) {
        return new CadFeature(feature.get("id").getAsString(), feature.get("kind").getAsString(), feature.get("label").getAsString(),
                string(feature, "class"), string(feature, "file"), feature.has("line") ? feature.get("line").getAsInt() : 0,
                feature.has("detail") && feature.get("detail").getAsBoolean(),
                feature.has("error") && feature.get("error").getAsBoolean(),
                read(feature.getAsJsonArray("children")));
    }

    private static @Nullable String string(JsonObject feature, String name) {
        JsonElement value = feature.get(name);
        return value == null || value.isJsonNull() ? null : value.getAsString();
    }

    /**
     * @return The sketch elements among the children
     */
    public long details() {
        return children.stream().filter(CadFeature::detail).count();
    }
}
