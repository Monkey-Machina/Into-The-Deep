//TODO: Refactor

package org.firstinspires.ftc.teamcode;


import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.util.Constants;
import com.pedropathing.util.DashboardPoseTracker;
import com.pedropathing.util.Drawing;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.AutoPaths.Auto_5_0_Paths;
import org.firstinspires.ftc.teamcode.Hardware.Constants.DepositConstants;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.Pedro.Constants.FConstants;
import org.firstinspires.ftc.teamcode.Pedro.Constants.LConstants;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Deposit;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.Claw;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Intake;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Robot;

@Autonomous(name = "Auto V0.1.2", group = "Competition")
public class Auto_5_0 extends OpMode {

    private Logger logger;
    private Hardware hardware;
    private Robot robot;
    private GamepadEx controller;
    private GamepadEx ghostController;
    private DashboardPoseTracker dashboardPoseTracker;
    private Follower follower;

    private enum AutoState{
        start,
        specDepoOne,
        intakingSpecOne,
        specDepoTwo,
        samplePushOne,
        samplePushTwo,
        samplePushThree,
        intakingSpecTwo,
        depositingSpecThree,
        intakingSpecThree,
        depositingSpecFour,
        intakingSpecFour,
        depositingSpecFive,
        park,
        done;
    }
    private AutoState autoState = AutoState.start;

    private enum SpecIntakingStatus {
        aligning,
        intaking
    }
    private SpecIntakingStatus specIntakingStatus = SpecIntakingStatus.aligning;

    private enum SpecDepoStatus {
        driving,
        releasing;
    }
    private SpecDepoStatus specDepoStatus = SpecDepoStatus.driving;

    private PathChain specDepoOnePC, specIntakeOnePC, specDepoTwoPC, samplePushOnePC, samplePushTwoPC, samplePushThreePC, specIntakeTwoPC, specDepoThreePC, specIntakeThreePC, specDepoFourPC,  specIntakeFourPC, specDepoFivePC, parkPC;

    @Override
    public void init() {
        // Pedro & Path Setup
        Auto_5_0_Paths.build();

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);

        follower.setStartingPose(Auto_5_0_Paths.startPose);
        dashboardPoseTracker = new DashboardPoseTracker(follower.poseUpdater);
        Drawing.drawRobot(follower.poseUpdater.getPose(), "#4CAF50");
        Drawing.sendPacket();
        buildPaths();

        try {
            Thread.sleep(350);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Robot Setup
        hardware = Hardware.getInstance();
        hardware.init(hardwareMap, true, true);
        Hardware.setInstance(hardware);

        controller = new GamepadEx(gamepad1);
        ghostController = new GamepadEx(gamepad2);


        logger = new Logger(telemetry, controller);

        robot = new Robot(hardware, ghostController,logger, true, false, true);

        robot.setDepositDesiredState(Deposit.State.specIntake);
        robot.setIntakeDesiredState(Intake.State.Stowed);
        robot.deposit.setClaw(Claw.State.Open);
        robot.deposit.slides.setPID(DepositConstants.spa, DepositConstants.sia, DepositConstants.sda, DepositConstants.sf);

    }

    @Override
    public void init_loop() {
        hardware.clearCache();
        controller.readButtons();

        robot.update();
        robot.command(0, 0);
        logger.logHeader("Auto Ready -- Init Loop");
        robot.log();
        logger.print();
    }

    @Override
    public void loop() {
        hardware.clearCache();
        autoStateUpdate();
        robot.update();
        controller.readButtons();
        dashboardPoseTracker.update();

        follower.update();
        robot.command(0, 0);

        logger.logHeader("Auto");
        logger.logData("Auto State", autoState, Logger.LogLevels.production);
        logger.logData("Robot Pose", follower.getPose(), Logger.LogLevels.production);
        logger.logData("Follower Is Busy", follower.isBusy(), Logger.LogLevels.production);
        logger.logData("T-Value", follower.getCurrentTValue(), Logger.LogLevels.production);
        logger.logData("Vx", follower.getVelocity().getXComponent(), Logger.LogLevels.production);
        logger.logData("ω", follower.poseUpdater.getAngularVelocity(), Logger.LogLevels.production);

        robot.log();

        logger.print();
        if (follower.getCurrentPath() != null) {
            Drawing.drawPath(follower.getCurrentPath(), "#808080");
        }

        Drawing.drawPoseHistory(dashboardPoseTracker, "#4CAF50");
        Drawing.drawRobot(follower.poseUpdater.getPose(), "#4CAF50");

    }

    public void autoStateUpdate(){
        switch (autoState) {
            case start:
                robot.deposit.setClaw(Claw.State.Closed);
                if (robot.deposit.claw.currentState == Claw.State.Closed) {
                    robot.setDepositDesiredState(Deposit.State.specDeposit);
                    follower.followPath(specDepoOnePC, true);
                    autoState = AutoState.specDepoOne;
                }
                break;

            case specDepoOne:
                depositSpec(specIntakeOnePC, AutoState.intakingSpecOne);
                break;

            case intakingSpecOne:
                intakeSpec(specDepoTwoPC, AutoState.specDepoTwo, 0.5, 0.3);
                break;

            case specDepoTwo:
                depositSpec(samplePushOnePC, AutoState.samplePushOne);
                break;

            case samplePushOne:
                if (!follower.isBusy() || follower.getPose().getX() <=18) {
                    follower.followPath(samplePushTwoPC);
                    autoState = AutoState.samplePushTwo;
                }

                break;

            case samplePushTwo:
                if (follower.getPose().getX() <=18 && follower.getCurrentTValue() >= 0.5) {
                    follower.followPath(samplePushThreePC, true);
                    autoState = AutoState.samplePushThree;
                }
                break;

            case samplePushThree:
                if (follower.getPose().getX() <=18 && follower.getCurrentTValue() >= 0.5) {
                    follower.followPath(specIntakeTwoPC, true);
                    autoState = AutoState.intakingSpecTwo;
                }
                break;

            case intakingSpecTwo:
                intakeSpec(specDepoThreePC, AutoState.depositingSpecThree , 0.7, 0.3);
                break;

            case depositingSpecThree:
                depositSpec(specIntakeThreePC, AutoState.intakingSpecThree);
                break;

            case intakingSpecThree:
                intakeSpec(specDepoFourPC, AutoState.depositingSpecFour , 0.5, 0.3);
                break;

            case depositingSpecFour:
                depositSpec(specIntakeFourPC, AutoState.intakingSpecFour);
                break;

            case intakingSpecFour:
                intakeSpec(specDepoFivePC, AutoState.depositingSpecFive , 0.5, 0.3);
                break;

            case depositingSpecFive:
                depositSpec(parkPC, AutoState.park);
                break;

            case park:
                if (!follower.isBusy()) {
                    autoState = AutoState.done;
                }
        }

    }

    private void buildPaths() {
        specDepoOnePC = follower
                .pathBuilder().addPath(Auto_5_0_Paths.specDepoOne)
                .setConstantHeadingInterpolation(Auto_5_0_Paths.specDepoOnePose.getHeading())
                .setPathEndTimeoutConstraint(250.0)
                .build();

        specIntakeOnePC =
                follower
                        .pathBuilder().addPath(Auto_5_0_Paths.specIntakeOne)
                        .setConstantHeadingInterpolation(Auto_5_0_Paths.specIntakeOnePose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();

        specDepoTwoPC =
                follower
                        .pathBuilder().addPath(Auto_5_0_Paths.specDepoTwo)
                        .setConstantHeadingInterpolation(Auto_5_0_Paths.specDepoTwoPose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();

        samplePushOnePC =
                follower
                        .pathBuilder().addPath(Auto_5_0_Paths.samplePushOne)
                        .setConstantHeadingInterpolation(Auto_5_0_Paths.pushPoseOne.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .setZeroPowerAccelerationMultiplier(10)
                        .build();

        samplePushTwoPC =
                follower
                        .pathBuilder().addPath(Auto_5_0_Paths.samplePushTwo)
                        .setConstantHeadingInterpolation(Auto_5_0_Paths.pushPoseTwo.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .setZeroPowerAccelerationMultiplier(10)
                        .build();

        samplePushThreePC =
                follower
                        .pathBuilder().addPath(Auto_5_0_Paths.samplePushThree)
                        .setConstantHeadingInterpolation(Auto_5_0_Paths.pushPoseThree.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .setZeroPowerAccelerationMultiplier(8)
                        .build();

        specIntakeTwoPC =
                follower
                        .pathBuilder().addPath(Auto_5_0_Paths.specIntakeTwo)
                        .setConstantHeadingInterpolation(Auto_5_0_Paths.specIntakeTwoPose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();

        specDepoThreePC =
                follower
                        .pathBuilder().addPath(Auto_5_0_Paths.specDepoThree)
                        .setConstantHeadingInterpolation(Auto_5_0_Paths.specDepoThreePose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();

        specIntakeThreePC =
                follower
                        .pathBuilder().addPath(Auto_5_0_Paths.specIntakeThree)
                        .setConstantHeadingInterpolation(Auto_5_0_Paths.specIntakeThreePose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();

        specDepoFourPC =
                follower
                        .pathBuilder().addPath(Auto_5_0_Paths.specDepoFour)
                        .setConstantHeadingInterpolation(Auto_5_0_Paths.specDepoFourPose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();

        specIntakeFourPC =
                follower
                        .pathBuilder().addPath(Auto_5_0_Paths.specIntakeFour)
                        .setConstantHeadingInterpolation(Auto_5_0_Paths.specIntakeFourPose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();

        specDepoFivePC =
                follower
                        .pathBuilder().addPath(Auto_5_0_Paths.specDepoFive)
                        .setConstantHeadingInterpolation(Auto_5_0_Paths.specDepoFivePose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();

        parkPC =
                follower
                        .pathBuilder().addPath(Auto_5_0_Paths.park)
                        .setConstantHeadingInterpolation(Auto_5_0_Paths.parkPose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();
    }

    private void intakeSpec(PathChain nextPath, AutoState nextAutoState, double slowTValue, double slowPower) {

        // While this function is running, it expects to start with maxPower at 1.0, and specIntaking status at aligning, and will end on these same conditions

        // If we are close to the end of the path, slow down to grab spec, only if in aligning state
        if (follower.getCurrentTValue() >= slowTValue && specIntakingStatus == SpecIntakingStatus.aligning) {
            follower.setMaxPower(slowPower);
        }

        // TODO: Tune the ω and vx thresholds for intaking specs
        // Deposit must always be at spec intake position, and robot velocity (both, vx and ω) must be below the threshold for intaking specs, and specIntakingStatus must be aligning
        if (robot.deposit.currentState == Deposit.State.specIntake && !follower.isBusy() && specIntakingStatus == SpecIntakingStatus.aligning) {
            robot.deposit.setClaw(Claw.State.Closed);
            specIntakingStatus = SpecIntakingStatus.intaking;
            follower.setMaxPower(1.0);
        }

        // If we are in the intaking phase, and the claw is closed, continue to next Path
        if (robot.deposit.claw.currentState == Claw.State.Closed && specIntakingStatus == SpecIntakingStatus.intaking) {
            robot.setDepositDesiredState(Deposit.State.specDeposit);
            follower.followPath(nextPath);
            autoState = nextAutoState;
            specIntakingStatus = SpecIntakingStatus.aligning;
        }

    }

    private void depositSpec(PathChain nextPath, AutoState nextAutoState) {

        follower.setCentripetalScaling(0.002);
        // If Vx meets velocity constraint and the path did not just start (t>=0.1) and specDepoStatus is driving, move to releasing status
        if ((!follower.isBusy() || (Math.abs(follower.getVelocity().getXComponent()) <= 1 && follower.getCurrentTValue() >= 0.8)) && specDepoStatus == SpecDepoStatus.driving) {
            robot.deposit.setClaw(Claw.State.Open);
            specDepoStatus = SpecDepoStatus.releasing;
        }

        // If the claw is open and specDepoStatus is releasing, move to next path.
        if (robot.deposit.claw.currentState == Claw.State.Open && specDepoStatus == SpecDepoStatus.releasing) {
            robot.setDepositDesiredState(Deposit.State.specIntake);
            follower.followPath(nextPath);
            autoState = nextAutoState;
            specDepoStatus = SpecDepoStatus.driving;
            follower.setCentripetalScaling(0.0012);
        }

    }

}