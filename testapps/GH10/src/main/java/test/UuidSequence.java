package test;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.Session;
import org.osgl.util.S;

import java.util.Vector;

public class UuidSequence extends Sequence implements SessionCustomizer {

    public UuidSequence() {
        super();
    }

    public UuidSequence(String name) {
        super(name);
    }

    @Override
    public void customize(Session session) {
        UuidSequence sequence = new UuidSequence("system-uuid");
        session.getLogin().addSequence(sequence);
    }

    @Override
    public boolean shouldAcquireValueAfterInsert() {
        return false;
    }

    @Override
    public boolean shouldUseTransaction() {
        return false;
    }

    @Override
    public Object getGeneratedValue(Accessor accessor, AbstractSession abstractSession, String s) {
        return S.uuid().toLowerCase().replaceAll("-","");
    }

    @Override
    public boolean shouldUsePreallocation() {
        return false;
    }

    @Override
    public Vector getGeneratedVector(Accessor accessor, AbstractSession abstractSession, String s, int i) {
        return null;
    }

    @Override
    public void onConnect() {
    }

    @Override
    public void onDisconnect() {
    }

}