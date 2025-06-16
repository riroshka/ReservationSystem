package ru.mivlgu.ReservationSystem.dto;

import java.util.List;

public class ReportData {
    private List<String> headers;
    private List<List<String>> rows;

    public ReportData(List<String> headers, List<List<String>> rows) {
        this.headers = headers;
        this.rows = rows;
    }

    public ReportData() {
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }
}
