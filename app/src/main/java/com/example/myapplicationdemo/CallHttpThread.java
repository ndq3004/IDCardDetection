package com.example.myapplicationdemo;

public class CallHttpThread extends  Thread{
    String s;
    Object syncToken;
    public  CallHttpThread(Object syncToken){
        this.s = "";
        this.syncToken = syncToken;
    }

    public void run(){
        while (true){
            synchronized (syncToken){
                try {
                    syncToken.wait();
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("My thread: " + s);
            }
        }
    }

    public void setText(String s){
        this.s = s;
    }
}
