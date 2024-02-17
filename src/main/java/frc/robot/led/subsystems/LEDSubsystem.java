// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.led.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.apriltagsCamera.Logger;

public class LEDSubsystem extends SubsystemBase {
  // These fields are static because shared between all virtual strings
  // The roboRIO can only support one AddressableLED object
  // m_led reflects the hardware device
  // m_ledBuffer is the buffer where we set up what we plan to send
  // m_currentMaxLength is the length of m_led and m_ledBuffer and needs to be increased as strings are added
  // m_commit is set to true to cause m_ledBuffer to be copied to m_led
  static private AddressableLED m_led = null; // new AddressableLED(0);
  static private AddressableLEDBuffer m_ledBuffer = null; // new AddressableLEDBuffer(2 * Constants.k_LEDLength);
  static private int m_currentMaxLength = 0;
  static private boolean m_commit = false; // Set this to cause buffer to be copied to hardware

  // These fields are specific to one virtual string
  private int m_startIdx; // where virtual string starts in m_led 
  private int m_size; // number of LEDs in virtual string
  private int m_width; // width of virtual string
  private int m_height; // height of virtual string

  /** Creates a new LEDSubsystem. */
  public LEDSubsystem(int startIdx, int width, int height) {
    // Set instance fields
    m_size = width * height;
    m_width = width;
    m_height = height;
    m_startIdx = startIdx;
    // This string uses [m_startIdx, m_startIdx + m_size) in m_ledBuffer

    // Now adjust the static fields
    if (m_led == null) { // First virtual string to be created
      m_led = new AddressableLED(/* PWM port */ 0);
    }
    int maxLength = m_startIdx + m_size;
    if (maxLength > m_currentMaxLength) { // We need to extend both m_led and m_ledBuffer 
      m_currentMaxLength = maxLength;
      m_ledBuffer = new AddressableLEDBuffer(m_currentMaxLength);
      m_led.setLength(m_currentMaxLength);
    }

    Logger.log("LEDSubsystem", 4, 
      String.format("start=%d, size=%d, width=%d, height=%d", 
      m_startIdx, m_size, m_width, m_height));

    start();
  }

  public LEDSubsystem(int startIdx, int size) {
    this(startIdx, size, 1);
  }

  /* 
   * This method scales the perceived intensity of a color.
   * This is a naive implementation as a placeholder for implementing something better later.
   */
  private Color scaleRgbIntensity(Color color, double scale) {
    return new Color(color.red * scale, color.green * scale, color.blue * scale);
  }

  public int getWidth() {
    return m_width;
  }

  public int getHeight() {
    return m_height;
  }

  public int getSize() {
    return m_size;
  }

  /*
   * Sets the color of a single LED on the string
   */
  public void setLED(int idx, Color color) {
    if ((idx >= 0) && (idx < m_size)) {
      // m_ledBuffer is shared so convert from relative index to absolute index
      m_ledBuffer.setLED(idx + m_startIdx, color);
    }
  }

  /*
   * Sets the color of a single LED for a 2 dimensional array of LEDs
   */
  public void setLED(int x, int y, Color color) {
    if ((x >= 0) && (x < m_width) && (y >= 0) && (y < m_height)) {
      if ((x & 1) == 1) {
        y = m_height - y - 1;
      }
      setLED((x * m_height) + y, color);
    }
  }

  /* 
   * Draws a single character on the string
   */
  public void drawChar(int ch, int x0, int y0, Color fgColor, Color bgColor) {
    if ((ch >= 0) && (ch < font.length / 5)) {
      for (int x = 0 ; x < 5 ; x++) {
        int v = font[(ch * 5) + x] & 0xff;

        for (int y = 0 ; y < 7 ; y++) {
          setLED(x + x0, y + y0, (v & 1) == 1 ? fgColor : bgColor);
          v >>= 1;
        }
      }
    }
  }

  /*
   * Draws a string of characters on the string
   */
  public void drawString(String text, int x, int y, Color fgColor, Color bgColor) {
    for (int i = 0 ; i < text.length() ; i++) {
      drawChar(text.charAt(i), x, y, fgColor, bgColor);
      x += 5;
    }
  }

  /*
   * Sets the color of a span of adjacent LEDs on the string
   */
  public void setLEDs(int start, int length, Color color) {
    int limit = start + length; // one past the end
    // Bring start and end into range for virtual string
    start = Math.max(start, 0);
    limit = Math.min(limit, m_size);
    Logger.log("LEDSubsystem", 0, String.format("setLEDs: %s [%d,%d) %s", getName(), start, limit, color.toString()));
    for (int i = start; i < limit; ++i) {
      setLED(i, color);
    }
  }

  /*
   * Set a range of adjacent LEDs with the centre LED(s) at the given color, and others dimmer.  
   */
  public void setLEDsMiddlePeak(int start, int length, Color color, double scale) {
    int limit = start + length; // one past the end
    // Bring start and end into range for virtual string
    start = Math.max(start, 0);
    limit = Math.min(limit, m_size);
    double maxDistance = Math.floor((length-1)/2);
    double middle = start + (length-1)/2;
    Logger.log("LEDSubsystem", 0, String.format("setLEDs: %s [%d,%d) %s", getName(), start, limit, color.toString()));
    for (int i = start; i < limit; ++i) {
      double distance = Math.floor(Math.abs(middle-i));
      double scale2 = 1 - (1-scale) * distance/maxDistance;
      setLED(i, scaleRgbIntensity(color, scale2));
    }
  }

  /*
   * Sets the color for all the LEDs on the string
   */
  public void setAllLEDs(Color color) {
    setLEDs(0, m_size, color);
  }

  /*
   * Commit the current changes to the string
   */
  public void commit() {
    m_commit = true;
  }

  /*
   * Copy a buffer to the internal buffer
   */

  // public void setLedData(AddressableLEDBuffer srcBuffer, int startIdx) {
  //   for (int i = 0; i < m_size; ++i) {
  //     Color color = srcBuffer.getLED(i);
  //     m_ledBuffer.setLED(i + startIdx, color);
  //   }
  // }

  /*
   * Copy the buffer to the hardware
   */
  // public void setData(AddressableLEDBuffer buffer) {
  //   setLedData(buffer, m_startIdx);

  //   m_led.setData(m_ledBuffer);
  // }

  public void start() {
    m_led.start();

  }

  public void end() {
    m_led.stop();
  }

  @Override
  public void periodic() {
    // If we have new data, let's commit
    // This flag is static so we only commit once per class regardless of how many instances exist
    if (m_commit) {
      m_led.setData(m_ledBuffer);
      m_commit = false;
    }
  }

  /* 
   * The font is a 5x7 font with 5 bytes per character
   */
  private final char[] font = new char[] {
      0x00, 0x00, 0x00, 0x00, 0x00,
      0x3E, 0x5B, 0x4F, 0x5B, 0x3E,
      0x3E, 0x6B, 0x4F, 0x6B, 0x3E,
      0x1C, 0x3E, 0x7C, 0x3E, 0x1C,
      0x18, 0x3C, 0x7E, 0x3C, 0x18,
      0x1C, 0x57, 0x7D, 0x57, 0x1C,
      0x1C, 0x5E, 0x7F, 0x5E, 0x1C,
      0x00, 0x18, 0x3C, 0x18, 0x00,
      0xFF, 0xE7, 0xC3, 0xE7, 0xFF,
      0x00, 0x18, 0x24, 0x18, 0x00,
      0xFF, 0xE7, 0xDB, 0xE7, 0xFF,
      0x30, 0x48, 0x3A, 0x06, 0x0E,
      0x26, 0x29, 0x79, 0x29, 0x26,
      0x40, 0x7F, 0x05, 0x05, 0x07,
      0x40, 0x7F, 0x05, 0x25, 0x3F,
      0x5A, 0x3C, 0xE7, 0x3C, 0x5A,
      0x7F, 0x3E, 0x1C, 0x1C, 0x08,
      0x08, 0x1C, 0x1C, 0x3E, 0x7F,
      0x14, 0x22, 0x7F, 0x22, 0x14,
      0x5F, 0x5F, 0x00, 0x5F, 0x5F,
      0x06, 0x09, 0x7F, 0x01, 0x7F,
      0x00, 0x66, 0x89, 0x95, 0x6A,
      0x60, 0x60, 0x60, 0x60, 0x60,
      0x94, 0xA2, 0xFF, 0xA2, 0x94,
      0x08, 0x04, 0x7E, 0x04, 0x08,
      0x10, 0x20, 0x7E, 0x20, 0x10,
      0x08, 0x08, 0x2A, 0x1C, 0x08,
      0x08, 0x1C, 0x2A, 0x08, 0x08,
      0x1E, 0x10, 0x10, 0x10, 0x10,
      0x0C, 0x1E, 0x0C, 0x1E, 0x0C,
      0x30, 0x38, 0x3E, 0x38, 0x30,
      0x06, 0x0E, 0x3E, 0x0E, 0x06,
      0x00, 0x00, 0x00, 0x00, 0x00, // Space
      0x00, 0x00, 0x5F, 0x00, 0x00,
      0x00, 0x07, 0x00, 0x07, 0x00,
      0x14, 0x7F, 0x14, 0x7F, 0x14,
      0x24, 0x2A, 0x7F, 0x2A, 0x12,
      0x23, 0x13, 0x08, 0x64, 0x62,
      0x36, 0x49, 0x56, 0x20, 0x50,
      0x00, 0x08, 0x07, 0x03, 0x00,
      0x00, 0x1C, 0x22, 0x41, 0x00,
      0x00, 0x41, 0x22, 0x1C, 0x00,
      0x2A, 0x1C, 0x7F, 0x1C, 0x2A,
      0x08, 0x08, 0x3E, 0x08, 0x08,
      0x00, 0x80, 0x70, 0x30, 0x00,
      0x08, 0x08, 0x08, 0x08, 0x08,
      0x00, 0x00, 0x60, 0x60, 0x00,
      0x20, 0x10, 0x08, 0x04, 0x02,
      0x3E, 0x51, 0x49, 0x45, 0x3E, // '0'
      0x00, 0x42, 0x7F, 0x40, 0x00,
      0x72, 0x49, 0x49, 0x49, 0x46,
      0x21, 0x41, 0x49, 0x4D, 0x33,
      0x18, 0x14, 0x12, 0x7F, 0x10,
      0x27, 0x45, 0x45, 0x45, 0x39,
      0x3C, 0x4A, 0x49, 0x49, 0x31,
      0x41, 0x21, 0x11, 0x09, 0x07,
      0x36, 0x49, 0x49, 0x49, 0x36,
      0x46, 0x49, 0x49, 0x29, 0x1E,
      0x00, 0x00, 0x14, 0x00, 0x00,
      0x00, 0x40, 0x34, 0x00, 0x00,
      0x00, 0x08, 0x14, 0x22, 0x41,
      0x14, 0x14, 0x14, 0x14, 0x14,
      0x00, 0x41, 0x22, 0x14, 0x08,
      0x02, 0x01, 0x59, 0x09, 0x06,
      0x3E, 0x41, 0x5D, 0x59, 0x4E,
      0x7C, 0x12, 0x11, 0x12, 0x7C,
      0x7F, 0x49, 0x49, 0x49, 0x36,
      0x3E, 0x41, 0x41, 0x41, 0x22,
      0x7F, 0x41, 0x41, 0x41, 0x3E,
      0x7F, 0x49, 0x49, 0x49, 0x41,
      0x7F, 0x09, 0x09, 0x09, 0x01,
      0x3E, 0x41, 0x41, 0x51, 0x73,
      0x7F, 0x08, 0x08, 0x08, 0x7F,
      0x00, 0x41, 0x7F, 0x41, 0x00,
      0x20, 0x40, 0x41, 0x3F, 0x01,
      0x7F, 0x08, 0x14, 0x22, 0x41,
      0x7F, 0x40, 0x40, 0x40, 0x40,
      0x7F, 0x02, 0x1C, 0x02, 0x7F,
      0x7F, 0x04, 0x08, 0x10, 0x7F,
      0x3E, 0x41, 0x41, 0x41, 0x3E,
      0x7F, 0x09, 0x09, 0x09, 0x06,
      0x3E, 0x41, 0x51, 0x21, 0x5E,
      0x7F, 0x09, 0x19, 0x29, 0x46,
      0x26, 0x49, 0x49, 0x49, 0x32,
      0x03, 0x01, 0x7F, 0x01, 0x03,
      0x3F, 0x40, 0x40, 0x40, 0x3F,
      0x1F, 0x20, 0x40, 0x20, 0x1F,
      0x3F, 0x40, 0x38, 0x40, 0x3F,
      0x63, 0x14, 0x08, 0x14, 0x63,
      0x03, 0x04, 0x78, 0x04, 0x03,
      0x61, 0x59, 0x49, 0x4D, 0x43,
      0x00, 0x7F, 0x41, 0x41, 0x41,
      0x02, 0x04, 0x08, 0x10, 0x20,
      0x00, 0x41, 0x41, 0x41, 0x7F,
      0x04, 0x02, 0x01, 0x02, 0x04,
      0x40, 0x40, 0x40, 0x40, 0x40,
      0x00, 0x03, 0x07, 0x08, 0x00,
      0x20, 0x54, 0x54, 0x78, 0x40,
      0x7F, 0x28, 0x44, 0x44, 0x38,
      0x38, 0x44, 0x44, 0x44, 0x28,
      0x38, 0x44, 0x44, 0x28, 0x7F,
      0x38, 0x54, 0x54, 0x54, 0x18,
      0x00, 0x08, 0x7E, 0x09, 0x02,
      0x18, 0xA4, 0xA4, 0x9C, 0x78,
      0x7F, 0x08, 0x04, 0x04, 0x78,
      0x00, 0x44, 0x7D, 0x40, 0x00,
      0x20, 0x40, 0x40, 0x3D, 0x00,
      0x7F, 0x10, 0x28, 0x44, 0x00,
      0x00, 0x41, 0x7F, 0x40, 0x00,
      0x7C, 0x04, 0x78, 0x04, 0x78,
      0x7C, 0x08, 0x04, 0x04, 0x78,
      0x38, 0x44, 0x44, 0x44, 0x38,
      0xFC, 0x18, 0x24, 0x24, 0x18,
      0x18, 0x24, 0x24, 0x18, 0xFC,
      0x7C, 0x08, 0x04, 0x04, 0x08,
      0x48, 0x54, 0x54, 0x54, 0x24,
      0x04, 0x04, 0x3F, 0x44, 0x24,
      0x3C, 0x40, 0x40, 0x20, 0x7C,
      0x1C, 0x20, 0x40, 0x20, 0x1C,
      0x3C, 0x40, 0x30, 0x40, 0x3C,
      0x44, 0x28, 0x10, 0x28, 0x44,
      0x4C, 0x90, 0x90, 0x90, 0x7C,
      0x44, 0x64, 0x54, 0x4C, 0x44,
      0x00, 0x08, 0x36, 0x41, 0x00,
      0x00, 0x00, 0x77, 0x00, 0x00,
      0x00, 0x41, 0x36, 0x08, 0x00,
      0x02, 0x01, 0x02, 0x04, 0x02,
      0x3C, 0x26, 0x23, 0x26, 0x3C,
      0x1E, 0xA1, 0xA1, 0x61, 0x12,
      0x3A, 0x40, 0x40, 0x20, 0x7A,
      0x38, 0x54, 0x54, 0x55, 0x59,
      0x21, 0x55, 0x55, 0x79, 0x41,
      0x21, 0x54, 0x54, 0x78, 0x41,
      0x21, 0x55, 0x54, 0x78, 0x40,
      0x20, 0x54, 0x55, 0x79, 0x40,
      0x0C, 0x1E, 0x52, 0x72, 0x12,
      0x39, 0x55, 0x55, 0x55, 0x59,
      0x39, 0x54, 0x54, 0x54, 0x59,
      0x39, 0x55, 0x54, 0x54, 0x58,
      0x00, 0x00, 0x45, 0x7C, 0x41,
      0x00, 0x02, 0x45, 0x7D, 0x42,
      0x00, 0x01, 0x45, 0x7C, 0x40,
      0xF0, 0x29, 0x24, 0x29, 0xF0,
      0xF0, 0x28, 0x25, 0x28, 0xF0,
      0x7C, 0x54, 0x55, 0x45, 0x00,
      0x20, 0x54, 0x54, 0x7C, 0x54,
      0x7C, 0x0A, 0x09, 0x7F, 0x49,
      0x32, 0x49, 0x49, 0x49, 0x32,
      0x32, 0x48, 0x48, 0x48, 0x32,
      0x32, 0x4A, 0x48, 0x48, 0x30,
      0x3A, 0x41, 0x41, 0x21, 0x7A,
      0x3A, 0x42, 0x40, 0x20, 0x78,
      0x00, 0x9D, 0xA0, 0xA0, 0x7D,
      0x39, 0x44, 0x44, 0x44, 0x39,
      0x3D, 0x40, 0x40, 0x40, 0x3D,
      0x3C, 0x24, 0xFF, 0x24, 0x24,
      0x48, 0x7E, 0x49, 0x43, 0x66,
      0x2B, 0x2F, 0xFC, 0x2F, 0x2B,
      0xFF, 0x09, 0x29, 0xF6, 0x20,
      0xC0, 0x88, 0x7E, 0x09, 0x03,
      0x20, 0x54, 0x54, 0x79, 0x41,
      0x00, 0x00, 0x44, 0x7D, 0x41,
      0x30, 0x48, 0x48, 0x4A, 0x32,
      0x38, 0x40, 0x40, 0x22, 0x7A,
      0x00, 0x7A, 0x0A, 0x0A, 0x72,
      0x7D, 0x0D, 0x19, 0x31, 0x7D,
      0x26, 0x29, 0x29, 0x2F, 0x28,
      0x26, 0x29, 0x29, 0x29, 0x26,
      0x30, 0x48, 0x4D, 0x40, 0x20,
      0x38, 0x08, 0x08, 0x08, 0x08,
      0x08, 0x08, 0x08, 0x08, 0x38,
      0x2F, 0x10, 0xC8, 0xAC, 0xBA,
      0x2F, 0x10, 0x28, 0x34, 0xFA,
      0x00, 0x00, 0x7B, 0x00, 0x00,
      0x08, 0x14, 0x2A, 0x14, 0x22,
      0x22, 0x14, 0x2A, 0x14, 0x08,
      0xAA, 0x00, 0x55, 0x00, 0xAA,
      0xAA, 0x55, 0xAA, 0x55, 0xAA,
      0x00, 0x00, 0x00, 0xFF, 0x00,
      0x10, 0x10, 0x10, 0xFF, 0x00,
      0x14, 0x14, 0x14, 0xFF, 0x00,
      0x10, 0x10, 0xFF, 0x00, 0xFF,
      0x10, 0x10, 0xF0, 0x10, 0xF0,
      0x14, 0x14, 0x14, 0xFC, 0x00,
      0x14, 0x14, 0xF7, 0x00, 0xFF,
      0x00, 0x00, 0xFF, 0x00, 0xFF,
      0x14, 0x14, 0xF4, 0x04, 0xFC,
      0x14, 0x14, 0x17, 0x10, 0x1F,
      0x10, 0x10, 0x1F, 0x10, 0x1F,
      0x14, 0x14, 0x14, 0x1F, 0x00,
      0x10, 0x10, 0x10, 0xF0, 0x00,
      0x00, 0x00, 0x00, 0x1F, 0x10,
      0x10, 0x10, 0x10, 0x1F, 0x10,
      0x10, 0x10, 0x10, 0xF0, 0x10,
      0x00, 0x00, 0x00, 0xFF, 0x10,
      0x10, 0x10, 0x10, 0x10, 0x10,
      0x10, 0x10, 0x10, 0xFF, 0x10,
      0x00, 0x00, 0x00, 0xFF, 0x14,
      0x00, 0x00, 0xFF, 0x00, 0xFF,
      0x00, 0x00, 0x1F, 0x10, 0x17,
      0x00, 0x00, 0xFC, 0x04, 0xF4,
      0x14, 0x14, 0x17, 0x10, 0x17,
      0x14, 0x14, 0xF4, 0x04, 0xF4,
      0x00, 0x00, 0xFF, 0x00, 0xF7,
      0x14, 0x14, 0x14, 0x14, 0x14,
      0x14, 0x14, 0xF7, 0x00, 0xF7,
      0x14, 0x14, 0x14, 0x17, 0x14,
      0x10, 0x10, 0x1F, 0x10, 0x1F,
      0x14, 0x14, 0x14, 0xF4, 0x14,
      0x10, 0x10, 0xF0, 0x10, 0xF0,
      0x00, 0x00, 0x1F, 0x10, 0x1F,
      0x00, 0x00, 0x00, 0x1F, 0x14,
      0x00, 0x00, 0x00, 0xFC, 0x14,
      0x00, 0x00, 0xF0, 0x10, 0xF0,
      0x10, 0x10, 0xFF, 0x10, 0xFF,
      0x14, 0x14, 0x14, 0xFF, 0x14,
      0x10, 0x10, 0x10, 0x1F, 0x00,
      0x00, 0x00, 0x00, 0xF0, 0x10,
      0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
      0xF0, 0xF0, 0xF0, 0xF0, 0xF0,
      0xFF, 0xFF, 0xFF, 0x00, 0x00,
      0x00, 0x00, 0x00, 0xFF, 0xFF,
      0x0F, 0x0F, 0x0F, 0x0F, 0x0F,
      0x38, 0x44, 0x44, 0x38, 0x44,
      0x7C, 0x2A, 0x2A, 0x3E, 0x14,
      0x7E, 0x02, 0x02, 0x06, 0x06,
      0x02, 0x7E, 0x02, 0x7E, 0x02,
      0x63, 0x55, 0x49, 0x41, 0x63,
      0x38, 0x44, 0x44, 0x3C, 0x04,
      0x40, 0x7E, 0x20, 0x1E, 0x20,
      0x06, 0x02, 0x7E, 0x02, 0x02,
      0x99, 0xA5, 0xE7, 0xA5, 0x99,
      0x1C, 0x2A, 0x49, 0x2A, 0x1C,
      0x4C, 0x72, 0x01, 0x72, 0x4C,
      0x30, 0x4A, 0x4D, 0x4D, 0x30,
      0x30, 0x48, 0x78, 0x48, 0x30,
      0xBC, 0x62, 0x5A, 0x46, 0x3D,
      0x3E, 0x49, 0x49, 0x49, 0x00,
      0x7E, 0x01, 0x01, 0x01, 0x7E,
      0x2A, 0x2A, 0x2A, 0x2A, 0x2A,
      0x44, 0x44, 0x5F, 0x44, 0x44,
      0x40, 0x51, 0x4A, 0x44, 0x40,
      0x40, 0x44, 0x4A, 0x51, 0x40,
      0x00, 0x00, 0xFF, 0x01, 0x03,
      0xE0, 0x80, 0xFF, 0x00, 0x00,
      0x08, 0x08, 0x6B, 0x6B, 0x08,
      0x36, 0x12, 0x36, 0x24, 0x36,
      0x06, 0x0F, 0x09, 0x0F, 0x06,
      0x00, 0x00, 0x18, 0x18, 0x00,
      0x00, 0x00, 0x10, 0x10, 0x00,
      0x30, 0x40, 0xFF, 0x01, 0x01,
      0x00, 0x1F, 0x01, 0x01, 0x1E,
      0x00, 0x19, 0x1D, 0x17, 0x12,
      0x00, 0x3C, 0x3C, 0x3C, 0x3C,
      0x00, 0x00, 0x00, 0x00, 0x00,
  };
}
