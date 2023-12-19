//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

//package edu.wayne.cs.severe.ir4se.processor.entity;

import java.util.List;
import java.util.Map;

public class RetrievalStats {
    private Double meanRecipRank;
    private Double meanPrecision;
    private Double meanRecall;
    private Double meanF1score;
    private Double meanAvgPrecision;
    private Double percentageTop1s;
    private Double percentageTop2s;
    private Double percentageTop3s;
    private Double percentageTop4s;
    private Double percentageTop5s;
    private Double percentageTop6s;
    private Double percentageTop7s;
    private Double percentageTop8s;
    private Double percentageTop9s;
    private Double percentageTop10s;
    private Double alpha;
    private Integer lambda;
    private Map<Query, List<Double>> queryStats;

    public RetrievalStats() {
    }

    public Double getMeanRecipRank() {
        return this.meanRecipRank;
    }

    public void setMeanRecipRank(Double meanRecipRank) {
        this.meanRecipRank = meanRecipRank;
    }

    public int getAmountOfQueries() {
        return this.queryStats.size();
    }

    public Map<Query, List<Double>> getQueryStats() {
        return this.queryStats;
    }

    public void setQueryStats(Map<Query, List<Double>> queryStats) {
        this.queryStats = queryStats;
    }

    public Double getMeanPrecision() {
        return this.meanPrecision;
    }

    public void setMeanPrecision(Double meanPrecision) {
        this.meanPrecision = meanPrecision;
    }

    public Double getMeanRecall() {
        return this.meanRecall;
    }

    public void setMeanRecall(Double meanRecall) {
        this.meanRecall = meanRecall;
    }

    public Double getMeanF1score() {
        return this.meanF1score;
    }

    public void setMeanF1score(Double meanF1score) {
        this.meanF1score = meanF1score;
    }

    public Double getMeanAvgPrecision() {
        return this.meanAvgPrecision;
    }

    public void setMeanAvgPrecision(Double meanAvgPrecision) {
        this.meanAvgPrecision = meanAvgPrecision;
    }

    public Double getPercentageTop1s() {
        return this.percentageTop1s;
    }

    public void setPercentageTop1s(Double percentageTop1s) {
        this.percentageTop1s = percentageTop1s;
    }

    public Double getPercentageTop2s() {
        return this.percentageTop2s;
    }

    public void setPercentageTop2s(Double percentageTop2s) {
        this.percentageTop2s = percentageTop2s;
    }

    public Double getPercentageTop3s() {
        return this.percentageTop3s;
    }

    public void setPercentageTop3s(Double percentageTop3s) {
        this.percentageTop3s = percentageTop3s;
    }

    public Double getPercentageTop4s() {
        return this.percentageTop4s;
    }

    public void setPercentageTop4s(Double percentageTop4s) {
        this.percentageTop4s = percentageTop4s;
    }

    public Double getPercentageTop5s() {
        return this.percentageTop5s;
    }

    public void setPercentageTop5s(Double percentageTop5s) {
        this.percentageTop5s = percentageTop5s;
    }

    public Double getPercentageTop6s() {
        return this.percentageTop6s;
    }

    public void setPercentageTop6s(Double percentageTop6s) {
        this.percentageTop6s = percentageTop6s;
    }

    public Double getPercentageTop7s() {
        return this.percentageTop7s;
    }

    public void setPercentageTop7s(Double percentageTop7s) {
        this.percentageTop7s = percentageTop7s;
    }

    public Double getPercentageTop8s() {
        return this.percentageTop8s;
    }

    public void setPercentageTop8s(Double percentageTop8s) {
        this.percentageTop8s = percentageTop8s;
    }

    public Double getPercentageTop9s() {
        return this.percentageTop9s;
    }

    public void setPercentageTop9s(Double percentageTop9s) {
        this.percentageTop9s = percentageTop9s;
    }

    public Double getPercentageTop10s() {
        return this.percentageTop10s;
    }

    public void setPercentageTop10s(Double percentageTop10s) {
        this.percentageTop10s = percentageTop10s;
    }

    public Double getAlpha() {
        return this.alpha;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    public Integer getLambda() {
        return this.lambda;
    }

    public void setLambda(Integer lambda) {
        this.lambda = lambda;
    }
}
