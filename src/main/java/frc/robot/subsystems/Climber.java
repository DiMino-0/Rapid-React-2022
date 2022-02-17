// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Climber extends SubsystemBase {
  /** Creates a new Climber. */
  private CANSparkMax climberMotor;
  private double speed;
  private Solenoid solenoid;

  ShuffleboardTab tab = Shuffleboard.getTab("Climber System");

  private NetworkTableEntry ClimberVRPM =     
  tab.add("Climber Velocity (RPM)", 0)
  .getEntry();
  private NetworkTableEntry ClimberVP =     
  tab.add("Climber Velocity (%)", this.speed)
  .getEntry();
  private NetworkTableEntry ClimberPos =     
  tab.add("Climber Position", 0)
  .getEntry();

  public Climber() {
    this.climberMotor = new CANSparkMax(Constants.CLIMBER_MOTOR, MotorType.kBrushless);

    motorReset();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    shuffleUpdate();
  }

  public void runClimber(double speed) {
    this.climberMotor.set(speed);
    this.speed = speed;
  }

  public void toggleSolClimber(Boolean bool)
  {
    this.solenoid.set(bool);
  }

  private void motorReset() {
    this.climberMotor.restoreFactoryDefaults();

    this.climberMotor.setIdleMode(IdleMode.kBrake);
  }

  public void shuffleUpdate() {
    this.ClimberVRPM.setDouble(this.climberMotor.getEncoder().getVelocity());
    this.ClimberVP.setDouble(this.speed);
    this.ClimberPos.setDouble(this.climberMotor.getEncoder().getPosition());
  }
}
