package org.firstinspires.ftc.teamcode.SystemsFSMs;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.util.Timing;

import org.firstinspires.ftc.teamcode.Hardware.Constants.IntakeConstants;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.Hardware.Util.PosChecker;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms.Bucket;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms.IntakeSlides;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms.SampleDetector;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Intake {
    private Logger logger;

    private IntakeSlides slides;
    public Bucket bucket;
    private SampleDetector detector;

    private GamepadEx controller;

    public enum State {
        Stowed,
        Deployed,
        Intaking;
    }

    public boolean passingThrough = false;

    public State targetState;
    public State currentState;

    private ArrayList<SampleDetector.SampleColor> acceptableColors =  new ArrayList<>();

    private double feedRate = 0.00;
    private double fedPosition = 0.00;
    private double maxFedPosition = IntakeConstants.maxExtensionPosition - IntakeConstants.readyPosition;
    private double minFedPosition = IntakeConstants.minIntakePosition - IntakeConstants.readyPosition;

    public boolean hasSample = false;
    public SampleDetector.SampleColor lastSeenColor;

    private Timing.Timer timer = new Timing.Timer(999999, TimeUnit.MILLISECONDS);
    private double recordedTime = 0.00;

    public boolean passthroughEject = false;

    public Intake(Hardware hardware, Logger logger, GamepadEx controller, boolean enableIntakeEncoderReset) {

        this.logger = logger;
        slides = new IntakeSlides(hardware, this.logger, enableIntakeEncoderReset);
        bucket = new Bucket(hardware, this.logger);
        detector = new SampleDetector(hardware, this.logger);
        this.controller = controller;
    }

    public void update() {
        bucket.update();
        slides.update();

        findsState();

        //TODO: Move this inside the Intake Switch Case
        // Only if intaking then update the detector
        if (currentState == State.Intaking) {
            detector.update();
        }

        recordedTime = timer.elapsedTime();
        timer.start();
    }

    public void command(double feedIn, double feedOut) {
        feed(feedIn, feedOut);
        switch (targetState)  {
            case Stowed:

                fedPosition = 0.00;
                stowedCommand();

                break;

            case Deployed:

                deployedCommand();

                break;

            case Intaking:

                intakeCommand();

                break;
        }

        bucket.command();
        slides.command();
    }

    public void log(){
        logger.logHeader("Intake");

        logger.logData("Current State", currentState, Logger.LogLevels.production);
        logger.logData("Target System State", targetState, Logger.LogLevels.production);
        logger.logData("Last seen color", lastSeenColor, Logger.LogLevels.production);

        logger.logData("Acceptable Colors", acceptableColors.toString(), Logger.LogLevels.debug);
        logger.logData("Has Sample", hasSample, Logger.LogLevels.debug);

        logger.logData("Feed Rate", feedRate, Logger.LogLevels.developer);
        logger.logData("Fed Position", fedPosition, Logger.LogLevels.developer);
        logger.logData("Recorded Time", recordedTime, Logger.LogLevels.developer);
        logger.logData("Passthrough Eject", passthroughEject, Logger.LogLevels.developer);


        bucket.log();
        detector.log();
        slides.log();
    }

    public void setTargetState(State state) {
        targetState = state;
    }

    public void setAcceptableColors(ArrayList<SampleDetector.SampleColor> colors) {
        acceptableColors = colors;
    }

    public void setLastSeenColor(SampleDetector.SampleColor color) {
        lastSeenColor = color;
    }

    public void setPassingThrough(boolean passing) {
        passingThrough = passing;
    }

    public void setPassthroughEject(boolean eject) {
        passthroughEject = eject;
    }

    public void setHasSample(boolean passing) {
        hasSample = false;
    }

    private void feed(double feedOut, double feedIn) {

        feedRate = IntakeConstants.maxFeedRate * (feedOut - feedIn);
        fedPosition += feedRate * ( recordedTime / 1000.0 );
        fedPosition = Math.min(Math.max(fedPosition, minFedPosition), maxFedPosition);
    }

    //TODO: This is messy, should refactor
    private void findsState() {

        if (PosChecker.atLinearPos(slides.getPosition(), 0.00, IntakeConstants.intakeSlidePositionTolerance) && targetState == State.Stowed && slides.getVelocity() <= 1) {
            currentState = State.Stowed;
        } else if (bucket.bucketCurrentState == Bucket.BucketState.Up) {
            currentState = State.Deployed;
        } else {
            currentState = State.Intaking;
        }

    }

    // Following xxxCommand methods contain functionality only for respective intake states. Do not contain certain cleanup tasks like buffer clearing
    private void stowedCommand() {
        bucket.setBucketTargetState(Bucket.BucketState.Up);
        if (bucket.bucketCurrentState == Bucket.BucketState.Up) {
            slides.setTargetCM(IntakeConstants.stowedPosition);
        }

        if (hasSample) {

            if (passingThrough) {

                if (passthroughEject) {
                    bucket.setRollerPower(IntakeConstants.passthroughPower);
                    bucket.setGateTargetState(Bucket.GateState.Open);
                } else {
                    bucket.setGateTargetState(Bucket.GateState.Compressed);
                    bucket.setRollerPower(IntakeConstants.stallingPower);
                }

            } else {
                bucket.setGateTargetState(Bucket.GateState.Closed);
                bucket.setRollerPower(IntakeConstants.stallingPower);
            }

        } else {
                
            bucket.setRollerPower(0.00);
            bucket.setGateTargetState(Bucket.GateState.Open);

        }

    }

    private void deployedCommand() {
        hasSample = false;
        detector.setState(SampleDetector.State.noSampleDetected);

        detector.clearDistanceBuffer();
        bucket.setTargetStates(Bucket.BucketState.Up, Bucket.GateState.Closed);
        slides.setTargetCM(IntakeConstants.readyPosition + fedPosition);

        bucket.setRollerPower(0.00);

    }

    private void intakeCommand() {

        // As long as bucket is past min bucket position to avoid chassis collision, bucket should be dow
        if (slides.getPosition() >= (IntakeConstants.minIntakePosition - 4) ){
            bucket.setBucketTargetState(Bucket.BucketState.Down);
        } else {
            bucket.setBucketTargetState(Bucket.BucketState.Up);
        }

        // Assign intake power
        if (bucket.bucketCurrentState == Bucket.BucketState.Down) {
            //TODO: Add intake reversing
            bucket.setRollerPower(IntakeConstants.intakingPower);
        } else {
            bucket.setRollerPower(0.00);
        }

        slides.setTargetCM(IntakeConstants.readyPosition + fedPosition);

        if (detector.state == SampleDetector.State.sampleDetected) {

            boolean acceptedSample = false;

            // Check is the color detected is part of the array of acceptable sample colors, assign acceptedSample boolean accordingly
            for (SampleDetector.SampleColor color : acceptableColors) {
                if (color == detector.color) {
                    acceptedSample = true;
                    break;
                }
            }

            // If the sample was accepted, stow the intake and mark hasSample as true
            if (acceptedSample) {

                targetState = State.Stowed;
                lastSeenColor = detector.color;
                hasSample = true;
                controller.gamepad.rumble(1.00, 1.00, 200);

                // If the sample color is unknown, keep gate closed. This essentially waits for a color to be determined
            } else if (detector.color != SampleDetector.SampleColor.unknown) {
                bucket.setGateTargetState(Bucket.GateState.Open);
            } else {
                bucket.setGateTargetState(Bucket.GateState.Closed);
            }

        } else {
            bucket.setGateTargetState(Bucket.GateState.Closed);
        }
    }

}

