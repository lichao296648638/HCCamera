package com.hushijie.hccamera.network;

/**
 * 更新app接口回显
 * Created by zhangkun on 2017/6/30.
 */

public class UpdateInfo {
    /**
     * doctor : {"forceVersion":"强制更新版本","currentVersion":"当前最新版本","url":"最新版本更新地址","message":"更新信息"}
     * patient : {"forceVersion":"强制更新版本","currentVersion":"当前最新版本","url":"最新版本更新地址","message":"更新信息"}
     */

    private DoctorEntity doctor;
    private PatientEntity patient;

    public DoctorEntity getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorEntity doctor) {
        this.doctor = doctor;
    }

    public PatientEntity getPatient() {
        return patient;
    }

    public void setPatient(PatientEntity patient) {
        this.patient = patient;
    }

    public static class DoctorEntity {
        /**
         * forceVersion : 强制更新版本
         * currentVersion : 当前最新版本
         * url : 最新版本更新地址
         * message : 更新信息
         */

        private int forceVersion;
        private int currentVersion;
        private String url;
        private String message;

        public int getForceVersion() {
            return forceVersion;
        }

        public void setForceVersion(int forceVersion) {
            this.forceVersion = forceVersion;
        }

        public int getCurrentVersion() {
            return currentVersion;
        }

        public void setCurrentVersion(int currentVersion) {
            this.currentVersion = currentVersion;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "DoctorEntity{" +
                    "forceVersion=" + forceVersion +
                    ", currentVersion=" + currentVersion +
                    ", url='" + url + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    public static class PatientEntity {
        /**
         * forceVersion : 强制更新版本
         * currentVersion : 当前最新版本
         * url : 最新版本更新地址
         * message : 更新信息
         */

        private int forceVersion;
        private int currentVersion;
        private String url;
        private String message;
        private boolean forceUpdate;

        public boolean isForceUpdate() {
            return forceUpdate;
        }

        public void setForceUpdate(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
        }

        public int getForceVersion() {
            return forceVersion;
        }

        public void setForceVersion(int forceVersion) {
            this.forceVersion = forceVersion;
        }

        public int getCurrentVersion() {
            return currentVersion;
        }

        public void setCurrentVersion(int currentVersion) {
            this.currentVersion = currentVersion;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "PatientEntity{" +
                    "forceVersion=" + forceVersion +
                    ", currentVersion=" + currentVersion +
                    ", url='" + url + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "UpdateInfo{" +
                "doctor=" + doctor +
                ", patient=" + patient +
                '}';
    }
}
