INSERT INTO slotstate(workflowid, date, status, externalid, retrycount) VALUES ('workflow-1', '2013-12-02T17:00:00.000Z', 'WAITING', null, 0);
INSERT INTO slotstate(workflowid, date, status, externalid, retrycount) VALUES ('workflow-1', '2013-12-02T18:00:00.000Z', 'READY', null, 14);
INSERT INTO slotstate(workflowid, date, status, externalid, retrycount) VALUES ('workflow-1', '2013-12-02T19:00:00.000Z', 'RUNNING', 'foo-bar', 0);
INSERT INTO slotstate(workflowid, date, status, externalid, retrycount) VALUES ('workflow-2', '2013-12-02T17:00:00.000Z', 'WAITING', null, 0);
INSERT INTO slotstate(workflowid, date, status, externalid, retrycount) VALUES ('workflow-2', '2013-12-02T18:00:00.000Z', 'READY', null, 0);
INSERT INTO slotstate(workflowid, date, status, externalid, retrycount) VALUES ('workflow-2', '2013-12-02T19:00:00.000Z', 'RUNNING', 'quux', 2);