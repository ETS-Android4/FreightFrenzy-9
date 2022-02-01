package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorGroup;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class VerticalLiftSubsystem extends SubsystemBase {
    private final MotorGroup liftMotors;

    public VerticalLiftSubsystem(HardwareMap hardwareMap) {
        Motor leftLift = hardwareMap.get(Motor.class, "liftLeft");
        Motor rightLift = hardwareMap.get(Motor.class, "liftRight");
        rightLift.setInverted(true);

        liftMotors = new MotorGroup(leftLift, rightLift);
    }

    public void liftToPosition(int position) {
        liftMotors.setTargetPosition(position);
    }

    public void setPower(double power) {
        liftMotors.set(power);
    }

    public void stop() {
        liftMotors.stopMotor();
    }

    public int getPosition() {
        return liftMotors.getCurrentPosition();
    }

    public boolean atTargetPosition() {
        return liftMotors.atTargetPosition();
    }

}
