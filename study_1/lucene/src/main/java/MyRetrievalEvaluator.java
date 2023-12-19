//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

//package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import edu.utdallas.seers.ir4se.evaluation.RetrievalEvaluation;
//import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalEvaluator;
//import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RelJudgment;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
//import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalStats;
import edu.wayne.cs.severe.ir4se.processor.exception.EvaluationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MyRetrievalEvaluator implements RetrievalEvaluator{
    /** @deprecated */
    @Deprecated
    private int corpusSize;
    /** @deprecated */
    @Deprecated
    private int queryResultLimit;

    public MyRetrievalEvaluator() {
    }

    /** @deprecated */
    @Deprecated
    public MyRetrievalEvaluator(int corpusSize) {
        this(corpusSize, Integer.MAX_VALUE);
    }

    /** @deprecated */
    @Deprecated
    public MyRetrievalEvaluator(int corpusSize, int queryResultLimit) {
        this.corpusSize = corpusSize;
        this.queryResultLimit = queryResultLimit;
    }

    public RetrievalEvaluation evaluateRelevanceJudgment(int hitsThreshold, RelJudgment relJudgment, List<RetrievalDoc> retrievedDocList) throws EvaluationException {
        List<Double> doubles = this.evaluateRelJudgment(relJudgment, retrievedDocList);
        return new RetrievalEvaluation(hitsThreshold, ((Double)doubles.get(0)).intValue(), (Double)doubles.get(7));
    }

    /** @deprecated */
    @Deprecated
    public List<Double> evaluateRelJudgment(RelJudgment relJudgment, List<RetrievalDoc> retrievedDocList) throws EvaluationException {
        if (relJudgment != null && retrievedDocList != null) {
            if (relJudgment.getRelevantDocs() == null) {
                throw new EvaluationException("The relevant judgements cannot be null");
            } else {
                boolean top1 = false;
                boolean top2 = false;
                boolean top3 = false;
                boolean top4 = false;
                boolean top5 = false;
                boolean top6 = false;
                boolean top7 = false;
                boolean top8 = false;
                boolean top9 = false;
                boolean top10 = false;
                List<Double> queryStats = new ArrayList();
                List<RetrievalDoc> relJudgDocs = relJudgment.getRelevantDocs();
                List<Integer> possibleRanks = new ArrayList();
                int numTruePos = 0;
                Iterator var10 = relJudgDocs.iterator();

                while(var10.hasNext()) {
                    RetrievalDoc relJudgDoc = (RetrievalDoc)var10.next();
                    int indexOf = retrievedDocList.indexOf(relJudgDoc);
                    if (indexOf != -1) {
                        possibleRanks.add(indexOf + 1);
                        ++numTruePos;
                    }

                    if (indexOf > -1 && indexOf < 10) {
                        top10 = true;
                        if (indexOf < 9) {
                            top9 = true;
                            if (indexOf < 8) {
                                top8 = true;
                                if (indexOf < 7) {
                                    top7 = true;
                                    if (indexOf < 6) {
                                        top6 = true;
                                        if (indexOf < 5) {
                                            top5 = true;
                                            if (indexOf < 4) {
                                                top4 = true;
                                                if (indexOf < 3) {
                                                    top3 = true;
                                                    if (indexOf < 2) {
                                                        top2 = true;
                                                        if (indexOf < 1) {
                                                            top1 = true;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                int numFalseNeg = relJudgDocs.size() - numTruePos;
                Collections.sort(possibleRanks);
                int rankFirst = 0;
                if (!possibleRanks.isEmpty()) {
                    rankFirst = (Integer)possibleRanks.get(0);
                }

                queryStats.add((double)rankFirst);
                double reciprocalRank = rankFirst != 0 ? 1.0 / (double)rankFirst : 0.0;
                queryStats.add(reciprocalRank);
                queryStats.add((double)numTruePos);
                double precision = retrievedDocList.size() == 0 ? 0.0 : (double)numTruePos / (double)retrievedDocList.size();
                queryStats.add(precision);
                queryStats.add((double)numFalseNeg);
                double recall = numTruePos + numFalseNeg == 0 ? 0.0 : (double)numTruePos / (double)(numTruePos + numFalseNeg);
                queryStats.add(recall);
                double f1Score = precision + recall == 0.0 ? 0.0 : 2.0 * precision * recall / (precision + recall);
                queryStats.add(f1Score);
                int numTP = 0;
                double sumPrecs = 0.0;

                double precAtRk;
                for(Iterator var23 = possibleRanks.iterator(); var23.hasNext(); sumPrecs += precAtRk) {
                    Integer rk = (Integer)var23.next();
                    ++numTP;
                    precAtRk = (double)numTP / (double)rk;
                }

                double avgPrec = (double)relJudgDocs.size() == 0.0 ? 0.0 : sumPrecs / (double)relJudgDocs.size();
                queryStats.add(avgPrec);
                queryStats.add(top1 ? 1.0 : 0.0);
                queryStats.add(top2 ? 1.0 : 0.0);
                queryStats.add(top3 ? 1.0 : 0.0);
                queryStats.add(top4 ? 1.0 : 0.0);
                queryStats.add(top5 ? 1.0 : 0.0);
                queryStats.add(top6 ? 1.0 : 0.0);
                queryStats.add(top7 ? 1.0 : 0.0);
                queryStats.add(top8 ? 1.0 : 0.0);
                queryStats.add(top9 ? 1.0 : 0.0);
                queryStats.add(top10 ? 1.0 : 0.0);
                return queryStats;
            }
        } else {
            throw new EvaluationException("The parameters cannot be null");
        }
    }

    public RetrievalStats evaluateModel(Map<Query, List<Double>> queryEvals) throws EvaluationException {
        return this.evaluateModel(queryEvals, false);
    }

    public RetrievalStats evaluateModel(Map<Query, List<Double>> queryEvals, boolean omitZeroRanks) throws EvaluationException {
        if (queryEvals == null) {
            throw new EvaluationException("No retrieval evaluation data");
        } else {
            RetrievalStats stats = new RetrievalStats();
            stats.setQueryStats(queryEvals);
            double[] sums = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
            int top1s = 0;
            int top2s = 0;
            int top3s = 0;
            int top4s = 0;
            int top5s = 0;
            int top6s = 0;
            int top7s = 0;
            int top8s = 0;
            int top9s = 0;
            int top10s = 0;
            Set<Map.Entry<Query, List<Double>>> entrySet = queryEvals.entrySet();
            if (omitZeroRanks) {
                entrySet = (Set)entrySet.stream().filter((e) -> {
                    return (Double)((List)e.getValue()).get(0) != 0.0;
                }).collect(Collectors.toSet());
            }

            Iterator var9 = entrySet.iterator();

            while(var9.hasNext()) {
                Map.Entry<Query, List<Double>> entry = (Map.Entry)var9.next();
                List<Double> queryStats = (List)entry.getValue();
                sums[0] += (Double)queryStats.get(1);
                sums[1] += (Double)queryStats.get(3);
                sums[2] += (Double)queryStats.get(5);
                sums[3] += (Double)queryStats.get(6);
                sums[4] += (Double)queryStats.get(7);
                if ((Double)queryStats.get(8) == 1.0) {
                    ++top1s;
                }

                if ((Double)queryStats.get(9) == 1.0) {
                    ++top2s;
                }

                if ((Double)queryStats.get(10) == 1.0) {
                    ++top3s;
                }
                if ((Double)queryStats.get(11) == 1.0) {
                    ++top4s;
                }
                if ((Double)queryStats.get(12) == 1.0) {
                    ++top5s;
                }
                if ((Double)queryStats.get(13) == 1.0) {
                    ++top6s;
                }
                if ((Double)queryStats.get(14) == 1.0) {
                    ++top7s;
                }
                if ((Double)queryStats.get(15) == 1.0) {
                    ++top8s;
                }
                if ((Double)queryStats.get(16) == 1.0) {
                    ++top9s;
                }
                if ((Double)queryStats.get(17) == 1.0) {
                    ++top10s;
                }
            }

            if (!entrySet.isEmpty()) {
                int amountOfQueries = entrySet.size();
                stats.setMeanRecipRank(sums[0] / (double)amountOfQueries);
                stats.setMeanPrecision(sums[1] / (double)amountOfQueries);
                stats.setMeanRecall(sums[2] / (double)amountOfQueries);
                stats.setMeanF1score(sums[3] / (double)amountOfQueries);
                stats.setMeanAvgPrecision(sums[4] / (double)amountOfQueries);
                stats.setPercentageTop1s((double)top1s / (double)amountOfQueries);
                stats.setPercentageTop2s((double)top2s / (double)amountOfQueries);
                stats.setPercentageTop3s((double)top3s / (double)amountOfQueries);
                stats.setPercentageTop4s((double)top4s / (double)amountOfQueries);
                stats.setPercentageTop5s((double)top5s / (double)amountOfQueries);
                stats.setPercentageTop6s((double)top6s / (double)amountOfQueries);
                stats.setPercentageTop7s((double)top7s / (double)amountOfQueries);
                stats.setPercentageTop8s((double)top8s / (double)amountOfQueries);
                stats.setPercentageTop9s((double)top9s / (double)amountOfQueries);
                stats.setPercentageTop10s((double)top10s / (double)amountOfQueries);
            } else {
                stats.setMeanRecipRank(0.0);
                stats.setMeanPrecision(0.0);
                stats.setMeanRecall(0.0);
                stats.setMeanF1score(0.0);
                stats.setMeanAvgPrecision(0.0);
            }

            return stats;
        }
    }

    public List<RetrievalDoc> filterRelevantDocs(RelJudgment relJudgment, List<RetrievalDoc> retrievedDocList) throws EvaluationException {
        if (relJudgment != null && retrievedDocList != null) {
            if (relJudgment.getRelevantDocs() == null) {
                throw new EvaluationException("The relevant judgements cannot be null");
            } else {
                List<RetrievalDoc> filteredList = new ArrayList();
                List<RetrievalDoc> relJudgDocs = relJudgment.getRelevantDocs();
                Iterator var5 = relJudgDocs.iterator();

                while(var5.hasNext()) {
                    RetrievalDoc relJudgDoc = (RetrievalDoc)var5.next();
                    int indexOf = retrievedDocList.indexOf(relJudgDoc);
                    if (indexOf != -1) {
                        filteredList.add((RetrievalDoc)retrievedDocList.get(indexOf));
                    } else {
                        relJudgDoc.setDocRank(0);
                        filteredList.add(relJudgDoc);
                    }
                }

                return filteredList;
            }
        } else {
            throw new EvaluationException("The parameters cannot be null");
        }
    }
}
