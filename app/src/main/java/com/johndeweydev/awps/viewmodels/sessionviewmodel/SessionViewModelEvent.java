package com.johndeweydev.awps.viewmodels.sessionviewmodel;

import com.johndeweydev.awps.viewmodels.ViewModelIOEvent;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.attackphase.ExecutionPhase;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.attackphase.InitializationPhase;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.attackphase.PostExecutionPhase;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.attackphase.TargetLockingPhase;

/**
 * Callbacks when a formatted serial data is received and processed by the session repository. This
 * formatted serial data contains information about the current state of the launcher
 *
 * @author John Dewey (johndewey02003@gmail.com)
 *
 * */
public interface SessionViewModelEvent extends ViewModelIOEvent,
        InitializationPhase, TargetLockingPhase, ExecutionPhase, PostExecutionPhase {

}
