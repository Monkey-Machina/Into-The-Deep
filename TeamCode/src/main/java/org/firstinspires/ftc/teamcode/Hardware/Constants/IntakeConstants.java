package org.firstinspires.ftc.teamcode.Hardware.Constants;

public class IntakeConstants {

    // Intake Slide PID Constants
    public static final double
            sp = 0.015,
            si = 0.00,
            sd = 0.0006;

    // Intake Slide Positions in CM
    public static final double
            stowedPosition = 0.00,
            readyPosition = 30.00,
            minIntakePosition = 18.00,
            maxExtensionPosition = 72.0,
            intakeSlidePositionTolerance = 1.8;

    // Max Intake Slide Manual Feed Rate in cm/s
    public static final double
            maxFeedRate = 200.00;

    public static final double
            intakeSlideZeroPower = -1.0,
            intakeSlideZeroStallPower = -0.15;


    // Bucket Servo Positions
    public static final double
            bucketUpPosition = 185.0,
            bucketDownPosition = 234.0,
            bucketEncLowerBound = 17.7818,
            bucketEncUpperBound = 339.4949,
            bucketEncPositionTolerance = 10.00;

    // Gate Positions
    public static final double
            gateOpenPosition = 147.0,
            gateClosedPosition = 265.5,
            gateCompressedPosition = 265.5,
            gateEncLowerBound = 40.6909,
            gateEncUpperBound = 317.0812,
            gateEncPositionTolerance = 5.00;

    // Intake Motor Powers
    public static final double
            intakingPower = 1.00,
            stallingPower = 0.450,
            passthroughPower = 1.0;

    // Detection Distance in mm
    public static final double
            detectionDistance = 28.00;

    // SampleDetector max values
    public static final double
            maxR = 3000.0,
            maxG = 6000.00,
            maxB = 4500.00,
            maxA = 4500.00;

}