//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

//package edu.wayne.cs.severe.ir4se.processor.controllers;

//import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RelJudgment;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
//import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalStats;
import edu.wayne.cs.severe.ir4se.processor.exception.EvaluationException;
import java.util.List;
import java.util.Map;

public interface RetrievalEvaluator {
    List<Double> evaluateRelJudgment(RelJudgment var1, List<RetrievalDoc> var2) throws EvaluationException;

    RetrievalStats evaluateModel(Map<Query, List<Double>> var1) throws EvaluationException;

    List<RetrievalDoc> filterRelevantDocs(RelJudgment var1, List<RetrievalDoc> var2) throws EvaluationException;
}
