package com.ddockddack.domain.gameRoom.repository;

public class RoundObserver implements Observer {

    private int memberCounter;
    private final String name;
    private final Subject gameMember;

    public RoundObserver(String name, Subject gameMember){
        this.name = name;
        this.gameMember = gameMember;
    }

    @Override
    public void update() {
        memberCounter++;
        String msg = (String) gameMember.getUpdate(this);
        System.out.println("Observer " + name + " got message: " + msg);
        System.out.println("Observer counts member... : " + memberCounter);
    }

}
