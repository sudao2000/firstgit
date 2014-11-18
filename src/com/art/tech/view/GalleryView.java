package com.art.tech.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

public class GalleryView extends Gallery 
{
	private Camera mCamera = new Camera();
	private int mMaxRotationAngle = 45;		// �����ת�Ƕ� 60
	private int mMaxZoom = -120;
	private int mCoveflowCenter;

	public GalleryView(Context context) 
	{
		super(context);
		this.setStaticTransformationsEnabled(true);
	}

	public GalleryView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		this.setStaticTransformationsEnabled(true);
	}

	public GalleryView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		this.setStaticTransformationsEnabled(true);
	}

	public int getMaxRotationAngle() 
	{
		return mMaxRotationAngle;
	}

	public void setMaxRotationAngle(int maxRotationAngle) 
	{
		mMaxRotationAngle = maxRotationAngle;
	}

	public int getMaxZoom() 
	{
		return mMaxZoom;
	}

	public void setMaxZoom(int maxZoom) 
	{
		mMaxZoom = maxZoom;
	}

	/** ��ȡGallery������x */
	private int getCenterOfCoverflow() 
	{
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
	}

	/** ��ȡView������x */
	private static int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	} 

	@Override
	protected boolean getChildStaticTransformation(View child, Transformation trans) 
	{
		final int childCenter = getCenterOfView(child);
		final int childWidth = child.getWidth();
		int rotationAngle = 0;

		trans.clear();
		trans.setTransformationType(Transformation.TYPE_BOTH);		// alpha �� matrix ���任

		if (childCenter == mCoveflowCenter) 
		{	// ���м��childView
			transformImageBitmap((ImageView) child, trans, 0);	
		} 
		else 
		{		// �����childView
			rotationAngle = (int) ( ( (float) (mCoveflowCenter - childCenter) / childWidth ) * mMaxRotationAngle );
			if (Math.abs(rotationAngle) > mMaxRotationAngle) 
			{
				rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle : mMaxRotationAngle;
			}
			transformImageBitmap((ImageView) child, trans, rotationAngle);
		}

		return true;
	}

	private void transformImageBitmap(ImageView child, Transformation trans, int rotationAngle) 
	{
		mCamera.save();
		
		final Matrix imageMatrix = trans.getMatrix();
		final int imageHeight = child.getLayoutParams().height;
		final int imageWidth = child.getLayoutParams().width;
		final int rotation = Math.abs(rotationAngle);

		// ��Z���������ƶ�camera���ӽǣ�ʵ��Ч��Ϊ�Ŵ�ͼƬ; �����Y�����ƶ�����ͼƬ�����ƶ�; X���϶�ӦͼƬ�����ƶ���
		mCamera.translate(0.0f, 0.0f, -20.0f);

		// As the angle of the view gets less, zoom in
		if (rotation < mMaxRotationAngle)
		{
			float zoomAmount = (float) (mMaxZoom + (rotation * 1.0));
			mCamera.translate(0.0f, 0.0f, zoomAmount);
		}

		mCamera.rotateY(rotationAngle);		// rotationAngle Ϊ������y��������ת�� Ϊ������y��������ת
		
		mCamera.getMatrix(imageMatrix);
		imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
		imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
		
		mCamera.restore();
	}
}