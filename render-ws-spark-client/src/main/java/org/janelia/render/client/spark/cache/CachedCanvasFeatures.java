package org.janelia.render.client.spark.cache;

import java.util.List;

import mpicbg.imagefeatures.Feature;

/**
 * Cache container for a canvas' list of features.
 *
 * @author Eric Trautman
 */
public class CachedCanvasFeatures implements CachedCanvasData {

    private final List<Feature> featureList;

    public CachedCanvasFeatures(final List<Feature> featureList) {
        this.featureList = featureList;
    }

    public List<Feature> getFeatureList() {
        return featureList;
    }

    public long getKilobytes() {
        return (long) (featureList.size() * AVERAGE_KILOBYTES_PER_FEATURE) + 1;
    }

    @Override
    public String toString() {
        return "featureList[" + featureList.size() + "]";
    }

    /** Since feature lists are only in-memory, this method is a no-op. */
    public void remove() {
    }

    /**
     * Average size of a feature.
     * This was derived from a 2K x 2K FAFB00 image and is hopefully good enough for most needs.
     */
    private static final double AVERAGE_KILOBYTES_PER_FEATURE = 0.6; // 600 bytes

}