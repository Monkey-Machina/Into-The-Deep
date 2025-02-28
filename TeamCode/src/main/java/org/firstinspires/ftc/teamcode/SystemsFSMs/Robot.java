//TODO: Refactor

package org.firstinspires.ftc.teamcode.SystemsFSMs;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.util.Timing;

import org.firstinspires.ftc.teamcode.Hardware.Constants.DepositConstants;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.Claw;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms.Bucket;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms.SampleDetector;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Robot {

    private Drivetrain drivetrain;
    private Deposit deposit;
    private Intake intake;

    private GamepadEx controller;
    private Logger logger;

    public enum States {
        cycling,
        handoff;
    }

    public enum Strategy {
        split,
        basket;
    }

    private Strategy strategy = Strategy.split;

    private States currentState;
    public Deposit.State depositDesiredState;
    public Intake.State intakeDesiredState;

    private boolean interference;
    private boolean clawSetForTransfer;

    private Timing.Timer passthroughTimer = new Timing.Timer(100000000, TimeUnit.MILLISECONDS);

    public Robot(Hardware hardware, GamepadEx controller, Logger logger, boolean intakeZeroing, boolean odometryEnabled) {

        this.controller = controller;
        this.logger = logger;

        drivetrain = new Drivetrain(hardware, controller, logger, odometryEnabled);
        deposit = new Deposit(hardware, logger);
        intake = new Intake(hardware, logger, intakeZeroing);

        deposit.setTargetState(Deposit.State.transfer);
        intake.setTargetState(Intake.State.Stowed);

        findState();
    }
    //TODO: Refactor
    public void update() {
        drivetrain.update();
        deposit.update();
        intake.update();
        findState();
    }
    //TODO: Refactor
    public void command() {

        switch (currentState) {
            case cycling:

                cycleIntakeLogic();
                cycleDepositLogic();

                clawSetForTransfer = false;

                break;

            case handoff:

                if (strategy == Strategy.basket || intake.lastSeenColor == SampleDetector.SampleColor.yellow) {
                    transferLogic();
                } else {
                    passthroughLogic();
                }

                break;

        }

        drivetrain.command();
        deposit.command();
        intake.command();

    }

    public void log() {
        logger.logHeader("--Robot--");

        logger.logData("Strategy", strategy, Logger.LogLevels.production);

        logger.logData("Current State", currentState, Logger.LogLevels.production);
        logger.logData("Deposit Desired State", depositDesiredState, Logger.LogLevels.production);
        logger.logData("Intake Desired State", intakeDesiredState, Logger.LogLevels.production);

        logger.logData("Interference", interference, Logger.LogLevels.debug);

        drivetrain.log();
        deposit.log();
        intake.log();
    }

    public void setDepositDesiredState(Deposit.State state) {
        depositDesiredState = state;
    }

    public void setIntakeDesiredState(Intake.State state) {
        intakeDesiredState = state;
    }

    public void goToDeposit() {

        if(strategy == Strategy.basket || intake.lastSeenColor == SampleDetector.SampleColor.yellow) {
            setDepositDesiredState(Deposit.State.sampleDeposit);
        // If the sample is known or red or blue, and the strategy is split, do specimens
        } else {
            if (deposit.targetState != Deposit.State.specIntake) {
                setDepositDesiredState(Deposit.State.specIntake);
            } else {
                setDepositDesiredState(Deposit.State.specDeposit);
            }
        }
    }

    public void setAcceptedSamples(ArrayList<SampleDetector.SampleColor> colors) {
        intake.setAcceptableColors(colors);
    }

    public void setStrategy(Robot.Strategy strategy) {
        this.strategy = strategy;
    }

    public void switchColor() {
        if (intake.lastSeenColor == SampleDetector.SampleColor.yellow) {
            intake.setLastSeenColor(SampleDetector.SampleColor.blue);
        } else {
            intake.setLastSeenColor(SampleDetector.SampleColor.yellow);
        }
    }

    private void findState() {

        // If either the Intake or Deposit isnt at their transfer ready positions then the state is cycling
        if (intake.currentState != Intake.State.Stowed || deposit.currentState != Deposit.State.transfer || !intake.hasSample) {
            currentState = States.cycling;

        } else { // If all of previous conditions are met, then we can assume we are ready to transfer
            currentState = States.handoff;
        }

    }
    //TODO: Refactor
    private void interferenceCheck() {

        boolean stowInterference = false;

        // If the intake is stowed, or is trying to stow, there is stow interference
        if (intake.getCurrentSystemState() == Intake.SystemState.Stowed || intake.getTargetSystemState() == Intake.SystemState.Stowed) {
            stowInterference = true;
        }

        // If there is stow interference, then check if the deposit is going to or from transfer
        // **Ignores potential for interference when going from spec intake, where you might be flipping the arm over too fast
        // For now, if there is stow interference and the slides are either below safe height, or trying to go below the safe height, we assume there is interference, but this is obviously not true, could be fixed by interference zones**
        if (stowInterference) {

            deposit.updateSlidesafe();

            if ((deposit.getSlideTargetCM() <= DepositConstants.slidePreTransferPos || deposit.getSlideCurrentCM() < DepositConstants.slidePreTransferPos - DepositConstants.slidePositionTolerance) && !(deposit.getSlidesDownSafe())) {

                interference = true;

            } else {
                interference = false;
            }

            if (depositDesiredState  == Deposit.State.specIntake && deposit.getSlideCurrentCM() <= DepositConstants.slidePreTransferPos - DepositConstants.slidePositionTolerance && (deposit.arm.getRightServoEncPos() <= DepositConstants.armRightEncSlideDownSafePos - DepositConstants.armRightPositionTolerance) && deposit.arm.getRightSetPosition() > DepositConstants.armRightSampleDepositPos) {
                interference  = true;
            }



        }
    }

    private void cycleIntakeLogic () {

        // If the intake was deployed, start intaking, if it was intaking, go back to deployed
        if (controller.wasJustPressed(GamepadKeys.Button.B)) {
            if (intake.targetState == Intake.State.Deployed) {
                intake.setTargetState(Intake.State.Intaking);
            } else {
                intake.setTargetState(Intake.State.Deployed);
            }
        }

        // If x pressed, stow intake
        if (controller.wasJustPressed(GamepadKeys.Button.X)) {
            intake.setTargetState(Intake.State.Stowed);
        }

    }

    private void cycleDepositLogic() {
        // If there is no interference potential, then all actions can be performed optimally for deposit
        if (!interference) {
            deposit.setTargetState(depositDesiredState);
        } else {

            //TODO: Add back in the routing for deposit if there is interference

//                    if (depositDesiredState == Deposit.State.specIntake) {
//                        deposit.setTargetState(Deposit.State.spec);
//                    } else if (depositDesiredState == Deposit.State.sampleDeposit) {
//                        deposit.setTargetState(Deposit.State.samplePreDeposit);
//                    } else {
//                        deposit.setTargetState(Deposit.State.preTransfer);
//                    }


        }
    }

    //TODO: Less sketchy fix for claw maybe
    private void transferLogic() {

        // TODO: This logic sets the transfer behind by a loop if the claw is already open before transfer begins
        deposit.setTargetState(Deposit.State.transfer);
        intake.setTargetState(Intake.State.Stowed);

        if (clawSetForTransfer) {
            deposit.setClaw(Claw.State.Closed);
            if (deposit.claw.currentState == Claw.State.Closed) {
                intake.hasSample = false;
            }
        } else {
            deposit.setClaw(Claw.State.Open);
            if (deposit.claw.currentState == Claw.State.Open) {
                clawSetForTransfer = true;
            }

        }
    }

    //TODO: this just like, doesnt work lmao
    private void passthroughLogic() {
        intake.setPassingThrough(true);
        if (intake.currentState == Intake.State.Stowed && intake.bucket.gateCurrentState == Bucket.GateState.Compressed) {
            intake.setHasSample(false);
        }

    }

}
