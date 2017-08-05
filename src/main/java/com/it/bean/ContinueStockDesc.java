package com.it.bean;

import com.google.common.base.MoreObjects;

public class ContinueStockDesc {
        private double percent;
        private String startDate;
        private String endDate;


        public ContinueStockDesc(double percent, String startDate, String endDate) {
            this.percent = percent;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public double getPercent() {
            return percent;
        }

        public void setPercent(double percent) {
            this.percent = percent;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("percent", percent)
                    .add("startDate", startDate)
                    .add("endDate", endDate).toString();
        }
    }