package org.firstinspires.ftc.teamcode.AutoPaths;

import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Point;

public class Auto_4_0_Paths {

    public static double intakeVxThreshold = 0.05, intakeVthetaThreshold = 0.05, depositVxThreshold = 0.00;

    public static Pose startPose = new Pose(6.299, 64.11415, Math.toRadians(180));

    public static Pose
        specDepoOnePose = new Pose(42.000, 78.000, Math.toRadians(180)),
        specDepoTwoPose = new Pose(42.000, 76.000, Math.toRadians(180)),
        specDepoThreePose = new Pose(42.000, 74.000, Math.toRadians(180)),
        specDepoFourPose = new Pose(42.000, 72.000, Math.toRadians(180)),
        specDepoFivePose = new Pose(42.000, 70.000, Math.toRadians(180)),

        specIntakeOnePose = new Pose(6.5,34.000, Math.toRadians(180)),
        specIntakeTwoPose = new Pose(6.5,34.000, Math.toRadians(180)),
        specIntakeThreePose = new Pose(6.5,34.000, Math.toRadians(180)),
        specIntakeFourPose = new Pose(6.5,34.000, Math.toRadians(180)),

        pushPoseOne = new Pose(0,23, Math.toRadians(180)),
        pushPoseTwo = new Pose(0,12, Math.toRadians(180)),
        pushPoseThree = new Pose(6.5,9, Math.toRadians(180)),

        parkPose =  new Pose(6.299,34.000, Math.toRadians(180));

    public static BezierLine
        specDepoOne,
        specDepoTwo,
        specDepoThree,
        specDepoFour,
        specDepoFive,
        park;

    public static BezierCurve
        specIntakeOne,
        samplePushOne,
        samplePushTwo,
        samplePushThree,
        specIntakeTwo,
        specIntakeThree,
        specIntakeFour;

    public  static void build() {
        specDepoOne = new BezierLine(startPose, specDepoOnePose);

        specIntakeOne = new BezierCurve(
                specDepoOnePose,
                new Pose(22.000, 67.000, Point.CARTESIAN),
                new Pose(28.000, 34.000, Point.CARTESIAN),
                new Pose(20.000, 34.000, Point.CARTESIAN),
                specIntakeOnePose
        );

        specDepoTwo = new BezierLine(specIntakeOnePose, specDepoTwoPose);

        samplePushOne = new BezierCurve(
                specDepoTwoPose,
                new Pose(17.000, 80.000, Point.CARTESIAN),
                new Pose(46.300, 64.300, Point.CARTESIAN),
                new Pose(12.500, 23.000, Point.CARTESIAN),
                new Pose(20.500, 12.000, Point.CARTESIAN),
                new Pose(62.300, 53.600, Point.CARTESIAN),
                new Pose(62.500, 53.800, Point.CARTESIAN),
                new Pose(73.000, 20.000, Point.CARTESIAN),
                new Pose(56.000, 21.000, Point.CARTESIAN),
                new Pose(68.300, 24.600, Point.CARTESIAN),
                pushPoseOne
        );

        samplePushTwo = new BezierCurve(
                pushPoseOne,
                new Pose(72.000, 26.000, Point.CARTESIAN),
                new Pose(68.000, 24.000, Point.CARTESIAN),
                new Pose(45.200, 11.500, Point.CARTESIAN),
                new Pose(84.000, 12.300, Point.CARTESIAN),
                pushPoseTwo
        );

        samplePushThree = new BezierCurve(
                pushPoseTwo,
                new Pose(50.000, 11.000, Point.CARTESIAN),
                new Pose(58.000, 18.000, Point.CARTESIAN),
                new Pose(70.00, pushPoseThree.getY(), Point.CARTESIAN),
                new Pose(62.500, pushPoseThree.getY(), Point.CARTESIAN),
                pushPoseThree
        );

        specIntakeTwo = new BezierCurve(
                pushPoseThree,
                new Pose(15.000, 15.000, Point.CARTESIAN),
                new Pose(19.300, 35.500, Point.CARTESIAN),
                new Pose(15.600, 34.000, Point.CARTESIAN),
                specIntakeTwoPose
        );

        specDepoThree = new BezierLine(
                specIntakeTwoPose,
                specDepoThreePose
        );

        specIntakeThree = new BezierCurve(
                specDepoThreePose,
                new Pose(22.000, 67.000, Point.CARTESIAN),
                new Pose(28.000, 34.000, Point.CARTESIAN),
                new Pose(20.000, 34.000, Point.CARTESIAN),
                specIntakeThreePose
        );

        specDepoFour = new BezierLine(
                specIntakeThreePose,
                specDepoFourPose
        );

        specIntakeFour = new BezierCurve(
                specDepoFourPose,
                new Pose(22.000, 67.000, Point.CARTESIAN),
                new Pose(28.000, 34.000, Point.CARTESIAN),
                new Pose(20.000, 34.000, Point.CARTESIAN),
                specIntakeFourPose
        );

        specDepoFive = new BezierLine(
                specIntakeFourPose,
                specDepoFivePose
        );

        park = new BezierLine(specDepoFivePose, parkPose);
        }

}
