package org.openlca.app.rcp;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Manager for the application images and icons.
 */
public class ImageManager {

	private static ImageRegistry imageRegistry = new ImageRegistry();

	public static Image getImage(ImageType imageType) {
		String path = imageType.getPath();
		Image image = imageRegistry.get(path);
		if (image != null && !image.isDisposed())
			return image;
		image = imageType.createImage();
		imageRegistry.put(path, image);
		return image;
	}

	public static ImageDescriptor getImageDescriptor(ImageType imageType) {
		String path = imageType.getPath();
		ImageDescriptor d = imageRegistry.getDescriptor(path);
		if (d != null)
			return d;
		d = RcpActivator.getImageDescriptor(path);
		imageRegistry.put(path, d);
		return d;
	}

	public static Image getImageWithOverlay(ImageType imageType,
			ImageType overlayType) {
		String path = imageType.getPath() + "-" + overlayType.getPath();
		Image imageWithOverlay = imageRegistry.get(path);
		if (imageWithOverlay != null && !imageWithOverlay.isDisposed())
			return imageWithOverlay;
		Image image = getImage(imageType);
		ImageDescriptor overlay = getImageDescriptor(overlayType);
		DecorationOverlayIcon imageWithIcon = new DecorationOverlayIcon(image,
				overlay, IDecoration.BOTTOM_RIGHT);
		imageWithOverlay = imageWithIcon.createImage();
		imageRegistry.put(path, imageWithOverlay);
		return imageWithOverlay;
	}

	public static ImageDescriptor getImageDescriptorWithOverlay(ImageType imageType,
			ImageType overlayType) {
		String path = imageType.getPath() + "-" + overlayType.getPath();
		ImageDescriptor imageWithOverlay = imageRegistry.getDescriptor(path);
		if (imageWithOverlay != null)
			return imageWithOverlay;
		ImageDescriptor image = getImageDescriptor(imageType);
		ImageDescriptor overlay = getImageDescriptor(overlayType);
		imageWithOverlay = new OverlayImageDescriptor(image, overlay);
		imageRegistry.put(path, imageWithOverlay);
		return imageWithOverlay;
	}

	private static class OverlayImageDescriptor extends CompositeImageDescriptor {

		private ImageDescriptor image;
		private ImageDescriptor overlay;
		private Point size;
		private Point overlaySize;

		private OverlayImageDescriptor(ImageDescriptor image, ImageDescriptor overlay) {
			this.image = image;
			this.overlay = overlay;
			Rectangle bounds = image.createImage().getBounds();
			size = new Point(bounds.width, bounds.height);
			bounds = overlay.createImage().getBounds();
			overlaySize = new Point(bounds.width, bounds.height);
		}

		@Override
		protected void drawCompositeImage(int width, int height) {
			drawImage(image.getImageData(), 0, 0);
			ImageData overlayData = overlay.getImageData();
			int x = size.x - overlaySize.x;
			int y = size.y - overlaySize.y;
			drawImage(overlayData, x, y);
		}

		@Override
		protected Point getSize() {
			return size;
		}

	}

}
