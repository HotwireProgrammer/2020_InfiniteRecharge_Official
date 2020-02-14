package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

public class Indexer {

    public DigitalInput beamBreakOne = new DigitalInput(5); // 3
    public DigitalInput beamBreakTwo = new DigitalInput(3); // 5
    public DigitalInput beamBreakTop = new DigitalInput(1); // 1

    boolean beamBreakClear = true;
    int ballCounter = 0;
    boolean shooterStarted = false;
    int loopCount = 0;

    public TalonSRX indexerFive = new TalonSRX(4);
    public TalonSRX floorBeltEight = new TalonSRX(3);

    public double floorBeltSpeed = 1.0f;
    public double indexerSpeed = 0.4f;

    public boolean firstAutomatic = false;

    private Shooter shooter;

    public Indexer(Shooter shooter) {
        this.shooter = shooter;
        firstAutomatic = true;
    }

    public void RunManualForward() {
        ballCounter = 0;
        loopCount = 0;
        shooterStarted = false;
        firstAutomatic = true;

        // DebugPrint();

        // ball - false
        // no ball - true
        System.out.println(" SHOOTER " + shooter.UpToSpeed() + " - BEAM " + beamBreakTop.get());
        if (shooter.UpToSpeed() || beamBreakTop.get()) {
            System.out.println("MOVING");
            indexerFive.set(ControlMode.PercentOutput, -indexerSpeed);
            floorBeltEight.set(ControlMode.PercentOutput, floorBeltSpeed * 0.5f);
        } else {
            System.out.println("STOPING");
            indexerFive.set(ControlMode.PercentOutput, 0.0f);
            floorBeltEight.set(ControlMode.PercentOutput, 0.0f);
        }
    }

    public void RunAutomatic() {

        DebugPrint();

        // Stop the indexer the first time we start automatic control
        if (firstAutomatic) {
            firstAutomatic = false;
            indexerFive.set(ControlMode.PercentOutput, 0.0f);
            floorBeltEight.set(ControlMode.PercentOutput, floorBeltSpeed);
        }

        if (beamBreakTwo.get()) {
            beamBreakClear = true;
        }

        if (ballCounter <= 3 && beamBreakTop.get()) {
            if (beamBreakOne.get() && !beamBreakTwo.get() && beamBreakClear) {
                beamBreakClear = false;
                if (shooterStarted) {
                    ballCounter++;
                    shooterStarted = false;
                }
                indexerFive.set(ControlMode.PercentOutput, 0.0f);
                floorBeltEight.set(ControlMode.PercentOutput, floorBeltSpeed);
            }

            if (!beamBreakOne.get()) {
                indexerFive.set(ControlMode.PercentOutput, -indexerSpeed);
                floorBeltEight.set(ControlMode.PercentOutput, 0.0f);
                shooterStarted = true;
            }

        } else if (!beamBreakTop.get()) {
            indexerFive.set(ControlMode.PercentOutput, 0.0f);
            floorBeltEight.set(ControlMode.PercentOutput, 0.0f);
        } else {
            if (!beamBreakOne.get() && beamBreakTop.get()) {
                if (loopCount <= 0) {
                    loopCount = 0;
                    indexerFive.set(ControlMode.PercentOutput, -indexerSpeed);
                    floorBeltEight.set(ControlMode.PercentOutput, floorBeltSpeed);
                }
                loopCount++;
            }

            if (loopCount >= 4 || !beamBreakTop.get()) {
                indexerFive.set(ControlMode.PercentOutput, 0.0f);
                floorBeltEight.set(ControlMode.PercentOutput, 0.0f);
            }
        }
    }

    public void DebugPrint() {
        System.out.println(" 1-" + beamBreakOne.get() + " 2-" + beamBreakTwo.get() + " top-" + beamBreakTop.get());
    }
}