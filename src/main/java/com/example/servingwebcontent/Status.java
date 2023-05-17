package com.example.servingwebcontent;

public class Status {
    public String name;
    public String status;
    public String priority;
    public String isDaemon;

    public Status(String string) {
        String parts[] = string.split(" ");
        for (int i = 0; i < Integer.parseInt(parts[0]); i++) {
            this.name = parts[1 + i * 4];
            this.status = parts[2 + i * 4];
            this.priority = parts[3 + i * 4];
            this.isDaemon = parts[4 + i * 4];
        }
    }

    public Status(String name, String status, String priority, String isDaemon) {
        this.name = name;
        this.status = status;
        this.priority = priority;
        this.isDaemon = isDaemon;
    }

    
    public String toString() {
        return "Status{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", isDaemon='" + isDaemon + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String isDaemon() {
        return isDaemon;
    }

    public void setDaemon(String isDaemon) {
        this.isDaemon = isDaemon;
    }

    
}
