package com.hjp.main.all.main.service.bean;

public class MakeCheckIn {

    private String makeTime;
    private String classRoomName;
    private String weekNumName;
    private String partName;

    public String getMakeTime() {
        return makeTime;
    }

    public void setMakeTime(String makeTime) {
        this.makeTime = makeTime;
    }

    public String getClassRoomName() {
        return classRoomName;
    }

    public void setClassRoomName(String classRoomName) {
        this.classRoomName = classRoomName;
    }

    public String getWeekNumName() {
        return weekNumName;
    }

    public void setWeekNumName(String weekNumName) {
        this.weekNumName = weekNumName;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    @Override
    public String toString() {
        return "MakeCheckIn{" +
                "makeTime='" + makeTime + '\'' +
                ", classRoomName='" + classRoomName + '\'' +
                ", weekNumName='" + weekNumName + '\'' +
                ", partName='" + partName + '\'' +
                '}';
    }
}
