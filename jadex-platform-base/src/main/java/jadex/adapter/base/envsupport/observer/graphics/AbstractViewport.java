package jadex.adapter.base.envsupport.observer.graphics;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.adapter.base.envsupport.observer.graphics.layer.ILayer;
import jadex.bridge.ILibraryService;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.MouseInputAdapter;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;


public abstract class AbstractViewport implements IViewport
{
	/** Axis inversion flag */
	protected IVector2			inversionFlag_;

	/** Canvas for graphical output. */
	protected Canvas			canvas_;

	/** Library service for loading resources. */
	protected ILibraryService	libService_;

	/** X-Coordinate of the viewport position. */
	protected IVector2			position_;

	/** Object shift x-coordinate. */
	protected float				objShiftX_;

	/** Object shift y-coordinate. */
	protected float				objShiftY_;

	/** Flag aspect ratio preservation. */
	protected boolean			preserveAR_;

	/** Size of the viewport without padding. */
	protected IVector2			size_;
	
	/** Maximum displayable area */
	protected IVector2			areaSize_;

	/** Real size of the viewport including padding. */
	protected IVector2			paddedSize_;

	/** Known drawable Objects. */
	protected Set				drawObjects_;

	/** Registered object layers. */
	protected SortedSet			objectLayers_;

	/** List of objects that should be drawn. */
	protected List				objectList_;

	/** Layers applied before drawable rendering */
	protected ILayer[]				preLayers_;

	/** Layers applied after drawable rendering */
	protected ILayer[]				postLayers_;

	/** The listeners of the viewport. */
	private Set					listeners_;

	public AbstractViewport()
	{
		inversionFlag_ = new Vector2Int(0);
		position_ = Vector2Double.ZERO.copy();
		preserveAR_ = true;
		size_ = new Vector2Double(1.0);
		areaSize_ = new Vector2Double(1.0);
		paddedSize_ = size_.copy();
		drawObjects_ = Collections.synchronizedSet(new HashSet());
		objectLayers_ = Collections.synchronizedSortedSet(new TreeSet());
		objectList_ = Collections.synchronizedList(new ArrayList());
		preLayers_ = new ILayer[0];
		postLayers_ = new ILayer[0];
		listeners_ = Collections.synchronizedSet(new HashSet());
	}

	/**
	 * Sets the current objects to draw.
	 * 
	 * @param objectList objects that should be drawn
	 */
	public void setObjectList(List objectList)
	{
		synchronized(objectList_)
		{
			objectList_.clear();
			objectList_.addAll(objectList);
		}
	}

	/**
	 * Returns the canvas that is used for displaying the objects.
	 */
	public Canvas getCanvas()
	{
		return canvas_;
	}

	/**
	 * Sets the pre-layers for the viewport.
	 * 
	 * @param layers the pre-layers
	 */
	public void setPreLayers(ILayer[] layers)
	{
		synchronized(preLayers_)
		{
			if(layers != null)
			{
				preLayers_ = layers;
			}
			else
			{
				preLayers_ = new ILayer[0];
			}
		}
	}

	/**
	 * Sets the post-layers for the viewport.
	 * 
	 * @param layers the post-layers
	 */
	public void setPostLayers(ILayer[] layers)
	{
		synchronized(postLayers_)
		{
			if(layers != null)
			{
				postLayers_ = layers;
			}
			else
			{
				postLayers_ = new ILayer[0];
			}
		}
	}

	/**
	 * Sets the size of the display area.
	 * 
	 * @param size size of the display area, may be padded to preserve aspect
	 *        ratio
	 */
	public void setSize(IVector2 size)
	{
		size_ = size;

		double width = 1.0;
		double height = 1.0;
		if(preserveAR_)
		{
			double sizeAR = size.getXAsDouble() / size.getYAsDouble();
			double windowAR = (double)canvas_.getWidth()
					/ (double)canvas_.getHeight();

			if(sizeAR > windowAR)
			{
				width = size.getXAsDouble();
				height = size.getXAsDouble() / windowAR;
			}
			else
			{
				width = size.getYAsDouble() * windowAR;
				height = size.getYAsDouble();
			}
		}
		else
		{
			width = size.getXAsDouble();
			height = size.getYAsDouble();
		}

		paddedSize_ = new Vector2Double(width, height);
	}
	
	/**
	 * Sets the maximum displayable size.
	 * 
	 * @param areaSize maximum area size.
	 */
	public void setAreaSize(final IVector2 areaSize)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				areaSize_ = areaSize;
				setSize(areaSize.copy());

				if (preserveAR_)
				{
					setPosition(paddedSize_.copy().subtract(areaSize_).multiply(0.5).negate());
				}
			}
		});
	}
	
	/**
	 * Returns the padded size
	 * @return padded size
	 */
	public IVector2 getPaddedSize()
	{
		return paddedSize_;
	}
	
	/**
	 * Returns the size of a pixel.
	 * @retun size of a pixel
	 */
	public IVector2 getPixelSize()
	{
		return paddedSize_.copy().divide(new Vector2Double(canvas_.getWidth(), canvas_.getHeight()));
	}
	
	/**
	 * Gets the position of the viewport.
	 */
	public IVector2 getPosition()
	{
		return position_.copy();
	}

	/**
	 * Sets the position of the viewport.
	 */
	public void setPosition(IVector2 pos)
	{
		position_ = pos;
	}

	public void setPreserveAspectRation(boolean preserveAR)
	{
		preserveAR_ = preserveAR;
		setSize(size_);
	}

	/**
	 * Returns true if the x-axis is inverted (right-left instead of
	 * left-right).
	 * 
	 * @return true, if the x-axis is inverted
	 */
	public boolean getInvertX()
	{
		return inversionFlag_.getXAsInteger() > 0;
	}

	/**
	 * Returns true if the y-axis is inverted (top-down instead of bottom-up).
	 * 
	 * @return true, if the y-axis is inverted
	 */
	public boolean getInvertY()
	{
		return inversionFlag_.getYAsInteger() > 0;
	}

	/**
	 * If set to true, inverts the x-axis (right-left instead of left-right).
	 * 
	 * @param b if true, inverts the x-axis
	 */
	public void setInvertX(boolean b)
	{
		if(b)
		{
			inversionFlag_ = new Vector2Int(1, inversionFlag_.getYAsInteger());
		}
		else
		{
			inversionFlag_ = new Vector2Int(0, inversionFlag_.getYAsInteger());
		}
	}

	/**
	 * If set to true, inverts the y-axis (top-down instead of bottom-up).
	 * 
	 * @param b if true, inverts the y-axis
	 */
	public void setInvertY(boolean b)
	{
		if(b)
		{
			inversionFlag_ = new Vector2Int(inversionFlag_.getXAsInteger(), 1);
		}
		else
		{
			inversionFlag_ = new Vector2Int(inversionFlag_.getXAsInteger(), 0);
		}
	}
	
	/**
	 * Gets the shift of all objects.
	 */
	public IVector2 getObjectShift()
	{
		return new Vector2Double(objShiftX_, objShiftY_);
	}

	/**
	 * Sets the shift of all objects.
	 */
	public void setObjectShift(IVector2 objectShift)
	{
		objShiftX_ = objectShift.getXAsFloat();
		objShiftY_ = objectShift.getYAsFloat();
	}

	/**
	 * Checks if this IViewport is showing on screen.
	 * 
	 * @return true if the IViewport is showing, false otherwise
	 */
	public boolean isShowing()
	{
		return canvas_.isShowing();
	}

	/**
	 * Adds a IViewportListener
	 * 
	 * @param listener new listener
	 */
	public void addViewportListener(IViewportListener listener)
	{
		listeners_.add(listener);
	}

	/**
	 * Removes a IViewportListener
	 * 
	 * @param listener the listener
	 */
	public void removeViewportListener(IViewportListener listener)
	{
		listeners_.remove(listener);
	}

	/**
	 * Fires a left mouse click event
	 * 
	 * @param position the clicked position
	 */
	private void fireLeftMouseClickEvent(IVector2 position)
	{
		synchronized(listeners_)
		{
			for(Iterator it = listeners_.iterator(); it.hasNext();)
			{
				IViewportListener listener = (IViewportListener)it.next();
				listener.leftClicked(position.copy());
			}
		}
	}

	/**
	 * Converts pixel coordinates into world coordinates
	 * 
	 * @param pixelX pixel x-coordinate
	 * @param pixelY pixel y-coordinate
	 * @return world coordinates
	 */
	private IVector2 getWorldCoordinates(int pixelX, int pixelY)
	{
		if(getInvertX())
		{
			pixelX = canvas_.getWidth() - pixelX;
		}

		if(getInvertY())
		{
			pixelY = canvas_.getHeight() - pixelY;
		}

		double xFac = (paddedSize_.getXAsDouble()) / canvas_.getWidth();
		double yFac = (paddedSize_.getYAsDouble()) / canvas_.getHeight();
		IVector2 position = new Vector2Double((xFac * pixelX) + position_.getXAsDouble(),
				(yFac * (canvas_.getHeight() - pixelY)) + position_.getYAsDouble());

		return position;
	}

	protected class MouseController extends MouseInputAdapter implements MouseWheelListener
	{
		private IVector2 lastDragPos;
		
		public MouseController()
		{
			lastDragPos = null;
		}
		
		public void mousePressed(MouseEvent e)
		{
			if(e.getButton() == MouseEvent.BUTTON1)
			{
				IVector2 position = getWorldCoordinates(e.getX(), e.getY());
				fireLeftMouseClickEvent(position);
			}
			else if(e.getButton() == MouseEvent.BUTTON3)
			{
				if (e.getClickCount() == 1)
				{
					lastDragPos = (new Vector2Double(e.getX(), e.getY())).multiply(getPixelSize());
				}
				else
				{
					setAreaSize(areaSize_);
				}
			}
		}
		
		public void mouseWheelMoved(MouseWheelEvent e)
		{
			IVector2 oldMousePos = getWorldCoordinates(e.getX(), e.getY());
			IVector2 zoomShift = size_.copy().multiply(0.1 * -e.getWheelRotation());
			IVector2 size = size_.copy().subtract(zoomShift);
			setSize(size);
			IVector2 newMousePos = getWorldCoordinates(e.getX(), e.getY());
			IVector2 pos = getPosition().copy().subtract(newMousePos.subtract(oldMousePos));
			setPosition(pos);
		}
		
		public void mouseDragged(MouseEvent e)
		{
			if (lastDragPos != null)
			{
				IVector2 position = (new Vector2Double(e.getX(), e.getY())).multiply(getPixelSize());
				IVector2 diff = position.copy().subtract(lastDragPos);
				if (getInvertX())
					diff.negateX();
				if (!getInvertY())
					diff.negateY();
				lastDragPos = position;
				setPosition(getPosition().copy().subtract(diff));
				
			}
		}
		
		public void mouseReleased(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON3)
				lastDragPos = null;
		}
	}
}
