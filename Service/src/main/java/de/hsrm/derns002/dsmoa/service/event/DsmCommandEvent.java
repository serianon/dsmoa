package de.hsrm.derns002.dsmoa.service.event;

public class DsmCommandEvent {

    private DsmCommand cmd;

    public DsmCommandEvent(DsmCommand cmd) {
        this.cmd = cmd;
    }

    public DsmCommand getCommand() {
        return cmd;
    }

}
