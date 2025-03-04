package org.firstinspires.ftc.teamcode.Hardware;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Hardware.Drivers.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.Hardware.Drivers.GobildaBlindToucherV69;

import java.util.List;

public class Hardware   {

    private static Hardware instance = null;
    private boolean enabled;

    private HardwareMap hardwareMap;

    // Chassis (Drivetrain, Pinpoint, Hubs)
    public DcMotorEx LF;
    public DcMotorEx RF;
    public DcMotorEx RB;
    public DcMotorEx LB;

    public GoBildaPinpointDriver pinPoint;

    List<LynxModule> hubs;

    // Deposit
    public DcMotorEx depositSlideRight;
    public DcMotorEx depositSlideLeft;

    public Servo armServo;
    public Servo wristServo;
    public Servo clawServo;

    public AnalogInput armEnc;
    public AnalogInput wristEnc;
    public AnalogInput clawEnc;

    // Intake
    public DcMotorEx intakeSlideMotor;
    public DcMotorEx intakeRoller;

    public Servo intakePivot;
    public Servo intakeGate;

    public AnalogInput intakePivotEnc;
    public AnalogInput intakeGateEnc;

    public GobildaBlindToucherV69 intakeCS;

    public DigitalChannel intakeLS;



    public static Hardware getInstance() {
        if (instance == null) {
            instance = new Hardware();
        }
        instance.enabled = true;
        return instance;
    }

    public void init(final HardwareMap map, boolean reset, boolean auto) {
        hardwareMap = map;

        // Drivetrain
        if (!auto) {
            RF = hardwareMap.get(DcMotorEx.class, "EH-Motor-3");
            RF.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            LF = hardwareMap.get(DcMotorEx.class, "EH-Motor-2");
            LF.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            LF.setDirection(DcMotorSimple.Direction.REVERSE);

            LB = hardwareMap.get(DcMotorEx.class, "EH-Motor-1");
            LB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            LB.setDirection(DcMotorSimple.Direction.REVERSE);

            RB = hardwareMap.get(DcMotorEx.class, "EH-Motor-0");
            RB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


            pinPoint = hardwareMap.get(GoBildaPinpointDriver.class, "CH-I2C-0-1");
            pinPoint.setOffsets(71, -135.325);
            pinPoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
            pinPoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
            if (reset) {
                pinPoint.resetPosAndIMU();
            }

        }

        hubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        // Deposit
        depositSlideRight = hardwareMap.get(DcMotorEx.class, "CH-Motor-0");
        depositSlideRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        depositSlideRight.setDirection(DcMotorSimple.Direction.REVERSE);
        if (reset) {
            depositSlideRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            depositSlideRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        depositSlideLeft = hardwareMap.get(DcMotorEx.class, "CH-Motor-1");
        depositSlideLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        depositSlideLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        if (reset) {
            depositSlideLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            depositSlideLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        armServo = hardwareMap.get(Servo.class, "CH-Servo-0");
        wristServo = hardwareMap.get(Servo.class, "CH-Servo-2");
        clawServo = hardwareMap.get(Servo.class, "CH-Servo-4");

        armEnc = hardwareMap.get(AnalogInput.class, "CH-Analog-0");
        wristEnc = hardwareMap.get(AnalogInput.class, "CH-Analog-1");
        clawEnc = hardwareMap.get(AnalogInput.class, "CH-Analog-2");

        // Intake
        intakeSlideMotor = hardwareMap.get(DcMotorEx.class, "CH-Motor-2");
        intakeSlideMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeSlideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        if (reset) {
            intakeSlideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            intakeSlideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        intakeRoller = hardwareMap.get(DcMotorEx.class, "CH-Motor-3");
        intakeRoller.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        intakePivot = hardwareMap.get(Servo.class, "EH-Servo-0");
        intakeGate = hardwareMap.get(Servo.class, "EH-Servo-2");

        intakePivotEnc = hardwareMap.get(AnalogInput.class, "EH-Analog-1");
        intakeGateEnc = hardwareMap.get(AnalogInput.class, "EH-Analog-0");

        intakeCS = hardwareMap.get(GobildaBlindToucherV69.class, "CH-I2C-1-0");
        intakeLS = hardwareMap.get(DigitalChannel.class, "CH-Digital-0");
    }

    public void init(final HardwareMap map, boolean auto) {
        init(map, true, auto);
    }

    public void clearCache() {
        for (LynxModule hub : hubs) {
            hub.clearBulkCache();
        }
    }

    public void Zero() {
        depositSlideRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        depositSlideRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        depositSlideLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        depositSlideLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intakeSlideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeSlideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        pinPoint.resetPosAndIMU();
    }

    public void zeroPinpoint() {
        pinPoint.resetPosAndIMU();
    }

}
