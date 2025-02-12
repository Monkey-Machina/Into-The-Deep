package org.firstinspires.ftc.teamcode.Hardware.Util;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.Servo;

public class AnalogServo {
    private Servo servo;
    private AnalogInput encoder;

    // Lower bound is @ pos 0, upper bound is @ pos 1, read out as an angle
    private double encoderLowerBound = 0.00;
    private double encoderUpperBound = 0.00;

    // Constant used to convert from a desired angle into a position to command
    private double conversionConstant = 0.00;

    public AnalogServo(Servo servo, AnalogInput encoder, double lowerBound, double upperBound) {
        this.servo = servo;
        this.encoder = encoder;

        encoderLowerBound = lowerBound;
        encoderUpperBound = upperBound;

        conversionConstant = 1.0 / (encoderUpperBound - encoderLowerBound);
    }

    public void setPos(double position) {
        // Clamps position to range achievable by servo, then converts angle into servo position
        double commandedPos = conversionConstant * ( ColorUtils.Clamp(encoderLowerBound, encoderUpperBound, position) - encoderLowerBound );

        servo.setPosition(commandedPos);
    }

    public double getPos() {
        return voltsToDegrees(encoder.getVoltage());
    }

    private double getCommandedPos() {
        return servo.getPosition();
    }

    private double voltsToDegrees(double volts) {
        return (volts/3.3) * 360;
    }


}
