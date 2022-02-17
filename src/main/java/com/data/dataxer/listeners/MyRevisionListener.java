package com.data.dataxer.listeners;

import com.data.dataxer.models.envers.WhoRevision;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.hibernate.envers.RevisionListener;

public class MyRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        WhoRevision rev = (WhoRevision) revisionEntity;
        rev.setUsername(SecurityUtils.loggedUser().getFirstName() + " " + SecurityUtils.loggedUser().getLastName());
    }
}
