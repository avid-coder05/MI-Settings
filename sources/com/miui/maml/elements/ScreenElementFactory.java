package com.miui.maml.elements;

import android.os.Build;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.elements.filament.PhysicallyBasedRenderingElement;
import com.miui.maml.elements.video.VideoElement;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class ScreenElementFactory {
    private FactoryCallback mFactoryCallback;

    /* loaded from: classes2.dex */
    public interface FactoryCallback {
        ScreenElement onCreateInstance(Element element, ScreenElementRoot screenElementRoot);
    }

    public ScreenElement createInstance(Element element, ScreenElementRoot screenElementRoot) {
        String tagName = element.getTagName();
        try {
            if (tagName.equalsIgnoreCase("Image")) {
                return new ImageScreenElement(element, screenElementRoot);
            }
            if (tagName.equalsIgnoreCase("Graphics")) {
                return new GraphicsElement(element, screenElementRoot);
            }
            if (tagName.equalsIgnoreCase("Time")) {
                return new TimepanelScreenElement(element, screenElementRoot);
            }
            if (!tagName.equalsIgnoreCase("ImageNumber") && !tagName.equalsIgnoreCase("ImageChars")) {
                if (tagName.equalsIgnoreCase("Text")) {
                    return new TextScreenElement(element, screenElementRoot);
                }
                if (tagName.equalsIgnoreCase("DateTime")) {
                    return new DateTimeScreenElement(element, screenElementRoot);
                }
                if (tagName.equalsIgnoreCase("Button")) {
                    return new ButtonScreenElement(element, screenElementRoot);
                }
                if (!tagName.equalsIgnoreCase("MusicControl") || Build.VERSION.SDK_INT < 21) {
                    if (!tagName.equalsIgnoreCase("ElementGroup") && !tagName.equalsIgnoreCase("Group")) {
                        if (tagName.equalsIgnoreCase("Var")) {
                            return new VariableElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("VarArray")) {
                            return new VariableArrayElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("AutoScaleGroup")) {
                            return new AutoScaleElementGroup(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("SpectrumVisualizer")) {
                            return new SpectrumVisualizerScreenElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("Slider")) {
                            return new AdvancedSlider(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("FramerateController")) {
                            return new FramerateController(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("FolmeConfig")) {
                            return new FolmeConfigElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("FolmeState")) {
                            return new FolmeStateElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("VirtualScreen")) {
                            return new VirtualScreen(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("VirtualElement")) {
                            return new VirtualAnimatedScreenElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("Line")) {
                            return new LineScreenElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("Rectangle")) {
                            return new RectangleScreenElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("Ellipse")) {
                            return new EllipseScreenElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("Circle")) {
                            return new CircleScreenElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("Arc")) {
                            return new ArcScreenElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("Curve")) {
                            return new CurveScreenElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("List")) {
                            return new ListScreenElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("Paint")) {
                            return new PaintScreenElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("Mirror")) {
                            return new MirrorScreenElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("Window")) {
                            return new WindowScreenElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("WebView")) {
                            return new WebViewScreenElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("Layer")) {
                            return new LayerScreenElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("GLLayer")) {
                            return new GLLayerScreenElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("Array")) {
                            return new ScreenElementArray(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("CanvasDrawer")) {
                            return new CanvasDrawerElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("Function")) {
                            return new FunctionElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("AnimConfig")) {
                            return new AnimConfigElement(element, screenElementRoot);
                        }
                        if (tagName.equalsIgnoreCase("AnimState")) {
                            return new AnimStateElement(element, screenElementRoot);
                        }
                        if (!tagName.equalsIgnoreCase("Video") || Build.VERSION.SDK_INT < 23) {
                            if (tagName.equalsIgnoreCase("Pbr")) {
                                return new PhysicallyBasedRenderingElement(element, screenElementRoot);
                            }
                            FactoryCallback factoryCallback = this.mFactoryCallback;
                            if (factoryCallback != null) {
                                return factoryCallback.onCreateInstance(element, screenElementRoot);
                            }
                            return null;
                        }
                        return new VideoElement(element, screenElementRoot);
                    }
                    return new ElementGroup(element, screenElementRoot);
                }
                return new MusicControlScreenElement(element, screenElementRoot);
            }
            return new ImageNumberScreenElement(element, screenElementRoot);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.w("ScreenElementFactory", "fail to create element." + e);
            return null;
        }
    }
}
