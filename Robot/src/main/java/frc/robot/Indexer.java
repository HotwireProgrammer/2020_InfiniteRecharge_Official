package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

public class Indexer {

    public DigitalInput beamBreakOne = new DigitalInput(0);
    public DigitalInput beamBreakTwo = new DigitalInput(2);

    boolean beamBreakClear = true;
    int ballCounter = 0;
    boolean shooterStarted = false;
    int loopCount = 0;

    public TalonSRX indexerFive = new TalonSRX(5);
    public TalonSRX floorBeltEight = new TalonSRX(8);

    public double floorBeltSpeed = 0.3f;
    public double indexerSpeed = 0.6f;

    public boolean firstAutomatic = false;

    public Indexer() {

    }

    public void RunManualForward() {
        ballCounter = 0;
        ballCounter = 0;
        loopCount = 0;
        shooterStarted = false;
        firstAutomatic = true;
        indexerFive.set(ControlMode.PercentOutput, -indexerSpeed);
        floorBeltEight.set(ControlMode.PercentOutput, -floorBeltSpeed * 0.5f);
    }

    public void RunAutomatic() {

        // Stop the indexer the first time we start automatic control
        if (firstAutomatic) {
            firstAutomatic = false;
            indexerFive.set(ControlMode.PercentOutput, 0.0f);
        }

        if (beamBreakTwo.get()) {
            beamBreakClear = true;
        }

        if (ballCounter <= 3) {
            if (beamBreakOne.get() && !beamBreakTwo.get() && beamBreakClear) {
                beamBreakClear = false;
                if (shooterStarted) {
                    ballCounter++;
                    shooterStarted = false;
                }
                indexerFive.set(ControlMode.PercentOutput, 0.0f);
            }

            if (!beamBreakOne.get()) {
                indexerFive.set(ControlMode.PercentOutput, -indexerSpeed);
                shooterStarted = true;
            }

            floorBeltEight.set(ControlMode.PercentOutput, -floorBeltSpeed);

        } else {
            if (!beamBreakOne.get()) {
                if (loopCount <= 0) {
                    loopCount = 0;
                    indexerFive.set(ControlMode.PercentOutput, -indexerSpeed);
                    floorBeltEight.set(ControlMode.PercentOutput, -floorBeltSpeed);
                }
                loopCount++;
            }

            if (loopCount >= 4) {
                indexerFive.set(ControlMode.PercentOutput, 0.0f);
                floorBeltEight.set(ControlMode.PercentOutput, 0.0f);
            }
        }
    }
}