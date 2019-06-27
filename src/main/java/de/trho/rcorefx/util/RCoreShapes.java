package de.trho.rcorefx.util;

import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

public class RCoreShapes {

  private RCoreShapes() {}

  public static Arc arc(double c, double r, double start, double end) {
    final Arc arc = new Arc();
    arc.setType(ArcType.ROUND);
    arc.setStrokeType(StrokeType.INSIDE);
    arc.setStartAngle(start);
    arc.setLength(end);
    arc.setCenterX(c);
    arc.setCenterY(c);
    arc.setRadiusX(r);
    arc.setRadiusY(r);
    arc.setSmooth(true);
    return arc;
  }

  public static Path pathOf(Paint fill, Paint stroke, final PathElement... elements) {
    final Path p = new Path();
    p.setStrokeType(StrokeType.CENTERED);
    p.setStrokeLineCap(StrokeLineCap.SQUARE);
    p.setFill(fill);
    p.setStroke(stroke);
    p.setFillRule(FillRule.EVEN_ODD);
    p.setSmooth(true);
    p.getElements().addAll(elements);
    return p;
  }

  public static MoveTo mvto(boolean abs, double x, double y) {
    final MoveTo m2 = new MoveTo();
    m2.setAbsolute(abs);
    m2.setX(x);
    m2.setY(y);
    return m2;
  }

  public static LineTo lnto(boolean abs, double x, double y) {
    final LineTo l2 = new LineTo();
    l2.setAbsolute(abs);
    l2.setX(x);
    l2.setY(y);
    return l2;
  }

  public static VLineTo vlnto(boolean abs, double y) {
    final VLineTo vl2 = new VLineTo();
    vl2.setAbsolute(abs);
    vl2.setY(y);
    return vl2;
  }

  public static HLineTo hlnto(boolean abs, double x) {
    final HLineTo hl2 = new HLineTo();
    hl2.setAbsolute(abs);
    hl2.setX(x);
    return hl2;
  }

  public static ArcTo arcto(boolean abs, double x, double y, double radiusX, double radiusY) {
    final ArcTo a2 = new ArcTo();
    a2.setAbsolute(abs);
    a2.setX(x);
    a2.setY(y);
    a2.setRadiusX(radiusX);
    a2.setRadiusY(radiusY);
    return a2;
  }

  public static ArcTo arcto(boolean abs, double x, double y, double radius) {
    final ArcTo a2 = new ArcTo();
    a2.setAbsolute(abs);
    a2.setX(x);
    a2.setY(y);
    a2.setRadiusX(radius);
    a2.setRadiusY(radius);
    return a2;
  }

  public static QuadCurveTo quadto(boolean abs, double x, double y, double cx, double cy) {
    final QuadCurveTo qc2 = new QuadCurveTo();
    qc2.setAbsolute(abs);
    qc2.setX(x);
    qc2.setY(y);
    qc2.setControlX(cx);
    qc2.setControlY(cy);
    return qc2;
  }

  public static QuadCurveTo quadto(boolean abs, double x, double y, double c) {
    final QuadCurveTo qc2 = new QuadCurveTo();
    qc2.setAbsolute(abs);
    qc2.setX(x);
    qc2.setY(y);
    qc2.setControlX(c);
    qc2.setControlY(c);
    return qc2;
  }

  public static CubicCurveTo cubicto(boolean abs, double x, double y, double c1, double c2) {
    final CubicCurveTo cc2 = new CubicCurveTo();
    cc2.setAbsolute(abs);
    cc2.setX(x);
    cc2.setY(y);
    cc2.setControlX1(c1);
    cc2.setControlY1(c1);
    cc2.setControlX2(c2);
    cc2.setControlY2(c2);
    return cc2;
  }

  public static CubicCurveTo cubicto(boolean abs, double x, double y, double cx1, double cx2,
      double cy1, double cy2) {
    final CubicCurveTo cc2 = new CubicCurveTo();
    cc2.setAbsolute(abs);
    cc2.setX(x);
    cc2.setY(y);
    cc2.setControlX1(cx1);
    cc2.setControlY1(cy1);
    cc2.setControlX2(cx2);
    cc2.setControlY2(cy2);
    return cc2;
  }

  public static ClosePath cp() {
    return new ClosePath();
  }

  public static MoveTo mvto(double x, double y) {
    final MoveTo m2 = new MoveTo();
    m2.setX(x);
    m2.setY(y);
    return m2;
  }

  public static LineTo lnto(double x, double y) {
    final LineTo l2 = new LineTo();
    l2.setX(x);
    l2.setY(y);
    return l2;
  }

  public static VLineTo vlnto(double y) {
    final VLineTo vl2 = new VLineTo();
    vl2.setY(y);
    return vl2;
  }

  public static HLineTo hlnto(double x) {
    final HLineTo hl2 = new HLineTo();
    hl2.setX(x);
    return hl2;
  }

  public static ArcTo arcto(double x, double y, double radiusX, double radiusY) {
    final ArcTo a2 = new ArcTo();
    a2.setX(x);
    a2.setY(y);
    a2.setRadiusX(radiusX);
    a2.setRadiusY(radiusY);
    return a2;
  }

  public static ArcTo arcto(double x, double y, double radius) {
    final ArcTo a2 = new ArcTo();
    a2.setX(x);
    a2.setY(y);
    a2.setRadiusX(radius);
    a2.setRadiusY(radius);
    return a2;
  }

  public static QuadCurveTo quadto(double x, double y, double cx, double cy) {
    final QuadCurveTo qc2 = new QuadCurveTo();
    qc2.setX(x);
    qc2.setY(y);
    qc2.setControlX(cx);
    qc2.setControlY(cy);
    return qc2;
  }

  public static QuadCurveTo quadto(double x, double y, double c) {
    final QuadCurveTo qc2 = new QuadCurveTo();
    qc2.setX(x);
    qc2.setY(y);
    qc2.setControlX(c);
    qc2.setControlY(c);
    return qc2;
  }

  public static CubicCurveTo cubicto(double x, double y, double c1, double c2) {
    final CubicCurveTo cc2 = new CubicCurveTo();
    cc2.setX(x);
    cc2.setY(y);
    cc2.setControlX1(c1);
    cc2.setControlY1(c1);
    cc2.setControlX2(c2);
    cc2.setControlY2(c2);
    return cc2;
  }

  public static CubicCurveTo cubicto(double x, double y, double cx1, double cx2, double cy1,
      double cy2) {
    final CubicCurveTo cc2 = new CubicCurveTo();
    cc2.setX(x);
    cc2.setY(y);
    cc2.setControlX1(cx1);
    cc2.setControlY1(cy1);
    cc2.setControlX2(cx2);
    cc2.setControlY2(cy2);
    return cc2;
  }

}
