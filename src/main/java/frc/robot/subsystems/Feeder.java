// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Feeder extends SubsystemBase {
  private Spark feederMotor;
  // Ultrasonic colorSensor;
  private double speed;

  ShuffleboardTab tab = Shuffleboard.getTab("Feeder System");

  private NetworkTableEntry FeederVP;

  /** Creates a new Feeder. */
  public Feeder() {
    this.feederMotor = new Spark(Constants.FEEDER_MOTOR);

    shuffleUpdate();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    shuffleUpdate();
  }

  public void runFeeder(double speed) {
    this.feederMotor.set(speed);
    
    this.speed = speed;
  }

  private void shuffleUpdate() {
    this.FeederVP.setDouble(this.speed);
  }
}