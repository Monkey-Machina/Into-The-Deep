//TODO: Refactor

package org.firstinspires.ftc.teamcode;


import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Pedro.Constants.FConstants;
import org.firstinspires.ftc.teamcode.Pedro.Constants.LConstants;

@Autonomous(name = "Auto V0.0.1", group = "Competition")
public class Auto  extends OpMode {

    private Telemetry telemetryA;

    public static double DISTANCE = 40;

    private Pose startPose = new Pose(6.299, 66.114, Math.toRadians(180));
    private Pose specDepositPose = new Pose(41.000, 74.000);

    public Path pushOne, pushTwo, pushThree, specIntakeOne, specDepoTwo;

    private Follower follower;

    private Path toSpec;

    private enum AutoState{
        start,
        toSpecDepo,
        pushOne,
        pushTwo,
        pushThree,
        specIntakeOne,
        specDepoTwo,
        done
    }

    private AutoState autoState = AutoState.start;

    @Override
    public void init() {
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);

        buildPaths();

        telemetryA = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        telemetryA.addLine("Ready");
        telemetryA.update();
    }

    @Override
    public void loop() {
        autoStateUpdate();
        follower.update();
        telemetryA.addData("Auto State", autoState);
        telemetryA.update();
    }

    public void buildPaths() {
        toSpec = new Path(new BezierLine(startPose, specDepositPose));
        toSpec.setConstantHeadingInterpolation(Math.toRadians(180));

        pushOne = new Path(
                new BezierCurve(
                        new Point(41.000, 74.000, Point.CARTESIAN),
                        new Point(15.5, 11.0, Point.CARTESIAN),
                        new Point(0.000, 54.739, Point.CARTESIAN),
                        new Point(113.989, 45.0, Point.CARTESIAN),
                        new Point(71.844, 18.039, Point.CARTESIAN),
                        new Point(12.000, 24.000, Point.CARTESIAN)
                )
        );
        pushOne.setConstantHeadingInterpolation(Math.toRadians(180));

        pushTwo = new Path(
                new BezierCurve(
                        new Point(12.000, 24.000, Point.CARTESIAN),
                        new Point(104.500, 25.200, Point.CARTESIAN),
                        new Point(71.500, 9.300, Point.CARTESIAN),
                        new Point(12.000, 12.000, Point.CARTESIAN)
                )
        );
        pushTwo.setConstantHeadingInterpolation(Math.toRadians(180));

        pushThree = new Path(
                new BezierCurve(
                        new Point(12.000, 12.000, Point.CARTESIAN),
                        new Point(98.000, 16.000, Point.CARTESIAN),
                        new Point(85.000, 4.200, Point.CARTESIAN),
                        new Point(7.000, 5.886, Point.CARTESIAN)
                )
        );
        pushThree.setConstantHeadingInterpolation(Math.toRadians(180));

        specIntakeOne = new Path(
                new BezierCurve(
                        new Point(7.000, 5.886, Point.CARTESIAN),
                        new Point(22.400, 6.000, Point.CARTESIAN),
                        new Point(31.413, 32.000, Point.CARTESIAN),
                        new Point(6.299, 34.000, Point.CARTESIAN)
                )
        );
        specIntakeOne.setConstantHeadingInterpolation(Math.toRadians(180));

        specDepoTwo = new Path(
                new BezierCurve(
                        new Point(6.299, 34.000, Point.CARTESIAN),
                        new Point(5.000, 46.000, Point.CARTESIAN),
                        new Point(22.400, 64.000, Point.CARTESIAN),
                        new Point(41.000, 70.000, Point.CARTESIAN)
                )
        );
        specDepoTwo.setConstantHeadingInterpolation(Math.toRadians(180));
    }

    public void autoStateUpdate(){
        switch (autoState) {
            case start:
                follower.followPath(toSpec);
                autoState = AutoState.toSpecDepo;
                break;

            case toSpecDepo:
                if (!follower.isBusy()) {
                    follower.followPath(pushOne);
                    autoState = AutoState.pushOne;
                }
                break;
            case pushOne:
                if (!follower.isBusy()) {
                    follower.followPath(pushTwo);
                    autoState = AutoState.pushTwo;
                }
                break;

            case pushTwo:
                if (!follower.isBusy()) {
                    follower.followPath(pushThree);
                    autoState = AutoState.pushThree;
                }
                break;

            case pushThree:
                if (!follower.isBusy()) {
                    follower.followPath(specIntakeOne);
                    autoState = AutoState.specIntakeOne;
                }
                break;
            case specIntakeOne:
                if (!follower.isBusy()) {
                    follower.followPath(specDepoTwo);
                    autoState = AutoState.specDepoTwo;
                }
                break;
            case specDepoTwo:
                if (!follower.isBusy()) {
                    autoState = AutoState.done;
                }
                break;
        }
    }
}