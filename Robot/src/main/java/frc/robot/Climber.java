package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jdk.jfr.Percentage;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Climber {

    public TalonSRX climberOne = new TalonSRX(49);
    public TalonSRX climberTwo = new TalonSRX(52);
    public DoubleSolenoid lockDoubleSolenoid = new DoubleSolenoid(6, 7);

    public float changeDelay = 0.2f;

    public Timer pancakeDelay = new Timer();

    public void coastMode(){
		climberOne.setNeutralMode(NeutralMode.Coast);
		climberTwo.setNeutralMode(NeutralMode.Coast);
    }

    public void brakeMode(){
		climberOne.setNeutralMode(NeutralMode.Brake);
        climberTwo.setNeutralMode(NeutralMode.Brake);
    }

    public void unlock() {
        lockDoubleSolenoid.set(DoubleSolenoid.Value.kReverse);
    }

    public void Reset() {
        pancakeDelay.reset();
        pancakeDelay.start();
    }

    public void climbMotors(float climbSpeed) {
        brakeMode();

        if (climbSpeed == 0.0f) {
            climberOne.set(ControlMode.PercentOutput, 0.0f);
            climberTwo.set(ControlMode.PercentOutput, 0.0f);
            if (pancakeDelay.get() > changeDelay) {
                lockDoubleSolenoid.set(DoubleSolenoid.Value.kForward);
            }
        } else {
            lockDoubleSolenoid.set(DoubleSolenoid.Value.kReverse);
            if (pancakeDelay.get() > changeDelay) {
                climberOne.set(ControlMode.PercentOutput, climbSpeed);
                climberTwo.set(ControlMode.PercentOutput, -climbSpeed);
            }
        }
    }
}
