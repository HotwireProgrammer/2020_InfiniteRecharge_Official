package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.DriveTrain;

public class Limelight {

    float inverted = 1;

    public Limelight() {

    }

    public boolean Position(DriveTrain driveTrain, float inverted, double xAdjust) {

        NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
        NetworkTableEntry tx = table.getEntry("tx");
        NetworkTableEntry ty = table.getEntry("ty");
        NetworkTableEntry ta = table.getEntry("ta");

        double x = tx.getDouble(0.0) + xAdjust;
        double y = ty.getDouble(0.0);
        double area = ta.getDouble(0.0);

        NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").setNumber(0);

        float maxArea = 3.4f;
        float minArea = 0.26f;
        float currentAreaPercentage = ((float) area - minArea) / (maxArea - minArea);

        float maxSpeed = 0.9f;
        float minSpeed = 0.7f;
        float currentSpeed = Lerp(minSpeed, maxSpeed, currentAreaPercentage);

        float turnBuffer = 1.3f;
        float turnSpeedSlow = -currentSpeed * 0.5f;

        if (x * inverted > turnBuffer) {

            driveTrain.SetLeftSpeed(currentSpeed * inverted);
            driveTrain.SetRightSpeed(turnSpeedSlow * inverted);

        } else if (x * inverted < -turnBuffer) {

            driveTrain.SetLeftSpeed(turnSpeedSlow * inverted);
            driveTrain.SetRightSpeed(currentSpeed * inverted);

        } else {

            driveTrain.SetLeftSpeed(0.0f);
            driveTrain.SetRightSpeed(0.0f);
            return true;

        }
        return false;
    }

    public float Lerp(float v0, float v1, float t) {

        if (t < 0) {
            t = 0;

        } else if (t > 1) {
            t = 1;
        }

        return (v0 + t * (v1 - v0));
    }
}