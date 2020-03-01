package com.deblock.minmax;

public class AlphaBetaZone {
    private Double min;
    private Double max;

    AlphaBetaZone() { }

    private AlphaBetaZone(AlphaBetaZone zone) {
        this.min = zone.min;
        this.max = zone.max;
    }

    public AlphaBetaZone cloneZone() {
        return new AlphaBetaZone(this);
    }

    public void newMinScore(double min) {
        if (this.min == null || min > this.min) {
           this.min = min;
        }
    }

    public void newMaxScore(double max) {
        if (this.max == null || max < this.max) {
            this.max = max;
        }
    }

    public boolean isLessThanExpectedMin(double childScore) {
        return this.min != null && childScore <= this.min;
    }

    public boolean isGreaterThanExpectedMax(double childScore) {
        return this.max != null && childScore >= this.max;
    }
}
