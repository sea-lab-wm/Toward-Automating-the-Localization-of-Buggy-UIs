//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

//package edu.wayne.cs.severe.ir4se.processor.entity;

import java.util.LinkedHashMap;
import java.util.Map;

public class Query extends edu.wayne.cs.severe.ir4se.processor.entity.Query {
    public static final String ISSUE_ID = "ISSUE_ID";
    private Integer queryId;
    private String txt;
    private String key;
    private Map<String, Object> info = new LinkedHashMap();

    public Query() {
    }

    public Query(int qId) {
        this.queryId = qId;
    }

    public Query(int qId, String txt) {
        this.queryId = qId;
        this.txt = txt;
    }

    public Integer getQueryId() {
        return this.queryId;
    }

    public String getKey() {
        return this.key;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public String getTxt() {
        return this.txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public Map<String, Object> getInfo() {
        return this.info;
    }

    public void addInfoAttribute(String key, Object value) {
        this.info.put(key, value);
    }

    public Object getInfoAttribute(String key) {
        return this.info.get(key);
    }

    public int hashCode() {
        boolean prime = true;
        int result = 1;
        result = 31 * result + (this.queryId == null ? 0 : this.queryId.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            Query other = (Query)obj;
            if (this.queryId == null) {
                if (other.queryId != null) {
                    return false;
                }
            } else if (!this.queryId.equals(other.queryId)) {
                return false;
            }

            return true;
        }
    }

    public String toString() {
        return "Q [id=" + this.queryId + ", txt=" + this.txt + ", key=" + this.key + ", info=" + this.info + "]";
    }

    public void copyInfo(Query query) {
        this.info = query.info;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isVoided() {
        return (this.getInfo() == null || this.getInfo().isEmpty()) && (this.getTxt() == null || this.getTxt().trim().isEmpty());
    }
}
