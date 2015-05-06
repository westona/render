package org.janelia.render.client;

import com.beust.jcommander.Parameter;

import java.io.IOException;
import java.util.Date;

import org.janelia.alignment.spec.stack.StackMetaData;
import org.janelia.alignment.spec.stack.StackVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.janelia.alignment.spec.stack.StackMetaData.StackState;

/**
 * Java client for managing stack meta data.
 *
 * @author Eric Trautman
 */
public class StackClient {

    public enum Action { CREATE, SET_STATE, DELETE }

    @SuppressWarnings("ALL")
    private static class Parameters extends RenderDataClientParameters {

        // NOTE: --baseDataUrl, --owner, and --project parameters defined in RenderDataClientParameters

        @Parameter(names = "--stack", description = "Stack name", required = true)
        private String stack;

        @Parameter(names = "--action", description = "CREATE, SET_STATE, or DELETE", required = true)
        private Action action;

        @Parameter(names = "--stackState", description = "LOADING, COMPLETE, or OFFLINE", required = false)
        private StackState stackState;

        @Parameter(names = "--versionNotes", description = "Notes about the version being created", required = false)
        private String versionNotes;

        @Parameter(names = "--cycleNumber", description = "Processing cycle number", required = false)
        private Integer cycleNumber;

        @Parameter(names = "--cycleStepNumber", description = "Processing cycle step number", required = false)
        private Integer cycleStepNumber;

        @Parameter(names = "--stackResoutionX", description = "X resoution (in nanometers) for the stack", required = false)
        private Double stackResoutionX;

        @Parameter(names = "--stackResoutionY", description = "Y resoution (in nanometers) for the stack", required = false)
        private Double stackResoutionY;

        @Parameter(names = "--stackResoutionZ", description = "Z resoution (in nanometers) for the stack", required = false)
        private Double stackResoutionZ;

        @Parameter(names = "--snapshotRootPath", description = "Root path for snapshot (only specify if offline snapshot should be stored)", required = false)
        private String snapshotRootPath;

    }

    /**
     * @param  args  see {@link Parameters} for command line argument details.
     */
    public static void main(String[] args) {
        try {

            final Parameters parameters = new Parameters();
            parameters.parse(args);

            LOG.info("main: entry, parameters={}", parameters);

            final StackClient client = new StackClient(parameters);

            if (Action.CREATE.equals(parameters.action)) {
                client.createStackVersion();
            } else if (Action.SET_STATE.equals(parameters.action)) {
                client.setStackState();
            } else if (Action.DELETE.equals(parameters.action)) {
                client.deleteStack();
            } else {
                throw new IllegalArgumentException("unknown action '" + parameters.action + "' specified");
            }

        } catch (final Throwable t) {
            LOG.error("main: caught exception", t);
            System.exit(1);
        }
    }

    private final Parameters params;

    private final String stack;
    private final RenderDataClient renderDataClient;

    public StackClient(final Parameters params) {

        this.params = params;
        this.stack = params.stack;
        this.renderDataClient = params.getClient();
    }

    public void createStackVersion()
            throws Exception {

        logMetaData("createStackVersion: before save");

        final StackVersion stackVersion = new StackVersion(new Date(),
                                                           params.versionNotes,
                                                           params.cycleNumber,
                                                           params.cycleStepNumber,
                                                           params.stackResoutionX,
                                                           params.stackResoutionY,
                                                           params.stackResoutionZ,
                                                           params.snapshotRootPath,
                                                           null);

        renderDataClient.saveStackVersion(stack, stackVersion);

        logMetaData("createStackVersion: after save");
    }

    public void setStackState()
            throws Exception {

        if (params.stackState == null) {
            throw new IllegalArgumentException("missing --stackState value");
        }

        logMetaData("setStackState: before update");

        renderDataClient.setStackState(stack, params.stackState);

        logMetaData("setStackState: after update");
    }

    public void deleteStack()
            throws Exception {
        logMetaData("deleteStack: before removal");
        renderDataClient.deleteStack(stack);
    }

    private void logMetaData(String context) {
        try {
            final StackMetaData stackMetaData = renderDataClient.getStackMetaData(stack);
            LOG.info("{}, stackMetaData={}", context, stackMetaData);
        } catch (IOException e) {
            LOG.info("{}, no meta data returned", context);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(StackClient.class);
}
