package de.perdian.apps.qifgenerator.fx.support.execution;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiExecutorImpl implements GuiExecutor {

    private static final Logger log = LoggerFactory.getLogger(GuiExecutorImpl.class);

    private List<GuiProgressListener> progressListeners = null;
    private List<GuiExecutorListener> executorListeners = null;
    private Executor executor = null;

    public GuiExecutorImpl() {
        this.setExecutorListeners(new CopyOnWriteArrayList<>());
        this.setProgressListeners(new CopyOnWriteArrayList<>());
        this.setExecutor(Executors.newFixedThreadPool(1));
    }

    @Override
    public void execute(GuiJob job) {
        this.getExecutorListeners().forEach(listener -> listener.onExecutionStarting());
        this.getExecutor().execute(() -> {
            try {
                job.execute((message, progress) -> this.getProgressListeners().forEach(progressListener -> progressListener.onProgress(message, progress)));
            } catch (Exception e) {
                log.error("Error occured while executing job: {}", job, e);
            } finally {
                this.getExecutorListeners().forEach(listener -> listener.onExecutionCompleted());
            }
        });
    }

    @Override
    public boolean addProgressListener(GuiProgressListener progressListener) {
        return this.getProgressListeners().add(progressListener);
    }
    @Override
    public boolean removeProgressListener(GuiProgressListener progressListener) {
        return this.getProgressListeners().remove(progressListener);
    }
    private List<GuiProgressListener> getProgressListeners() {
        return this.progressListeners;
    }
    private void setProgressListeners(List<GuiProgressListener> progressListeners) {
        this.progressListeners = progressListeners;
    }

    @Override
    public boolean addExecutorListener(GuiExecutorListener executorListener) {
        return this.getExecutorListeners().add(executorListener);
    }
    @Override
    public boolean removeExecutorListener(GuiExecutorListener executorListener) {
        return this.getExecutorListeners().remove(executorListener);
    }
    private List<GuiExecutorListener> getExecutorListeners() {
        return this.executorListeners;
    }
    private void setExecutorListeners(List<GuiExecutorListener> executorListeners) {
        this.executorListeners = executorListeners;
    }

    private Executor getExecutor() {
        return this.executor;
    }
    private void setExecutor(Executor executor) {
        this.executor = executor;
    }

}
