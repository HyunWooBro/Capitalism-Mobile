package core.scene.stage.actor.widget.utils;

public class Align {
	
	public static enum HAlign {
		LEFT, 
		CENTER, 
		RIGHT, 
	}
	
	public static enum VAlign {
		TOP,  
		CENTER, 
		BOTTOM, 
	}
	
	private static final int ALIGNMENT_CENTER 	= 0;
	private static final int ALIGNMENT_TOP 		= 1 << 0;
	private static final int ALIGNMENT_BOTTOM	= 1 << 1;
	private static final int ALIGNMENT_LEFT 		= 1 << 2;
	private static final int ALIGNMENT_RIGHT 		= 1 << 3;
	
	public static final Align CENTER = new ImmutableAlign(ALIGNMENT_CENTER);
	public static final Align WEST = new ImmutableAlign(ALIGNMENT_LEFT);
	public static final Align EAST = new ImmutableAlign(ALIGNMENT_RIGHT);
	public static final Align NORTH = new ImmutableAlign(ALIGNMENT_TOP);
	public static final Align SOUTH = new ImmutableAlign(ALIGNMENT_BOTTOM);
	public static final Align NORTH_WEST = new ImmutableAlign(ALIGNMENT_TOP | ALIGNMENT_LEFT);
	public static final Align NORTH_EAST = new ImmutableAlign(ALIGNMENT_TOP | ALIGNMENT_RIGHT);
	public static final Align SOUTH_WEST = new ImmutableAlign(ALIGNMENT_BOTTOM | ALIGNMENT_LEFT);
	public static final Align SOUTH_EAST = new ImmutableAlign(ALIGNMENT_BOTTOM | ALIGNMENT_RIGHT);
	
	private int mAlignment;
	
	private Align(int alignment) {
		mAlignment = alignment;
	}

	public Align() {
	}
	
	public Align(HAlign align) {
		set(align);
	}
	
	public Align(VAlign align) {
		set(align);
	}
	
	public Align(Align align) {
		set(align);
	}
	
	public void set(HAlign align) {
		switch(align) {
			case CENTER:	center();
				break;
			case LEFT:		left();
				break;
			case RIGHT:		right();
				break;
		}
	}
	
	public void set(VAlign align) {
		switch(align) {
			case BOTTOM:	bottom();
				break;
			case CENTER:	center();
				break;
			case TOP:		top();
				break;
		}
	}
	
	public void set(Align align) {
		mAlignment = align.mAlignment;
	}
	
	public void center() {
		mAlignment = Align.ALIGNMENT_CENTER;
	}
	
	public void top() {
		mAlignment |= Align.ALIGNMENT_TOP;
		mAlignment &= ~Align.ALIGNMENT_BOTTOM;
	}

	public void left() {
		mAlignment |= Align.ALIGNMENT_LEFT;
		mAlignment &= ~Align.ALIGNMENT_RIGHT;
	}
	
	public void right() {
		mAlignment |= Align.ALIGNMENT_RIGHT;
		mAlignment &= ~Align.ALIGNMENT_LEFT;
	}
	
	public void bottom() {
		mAlignment |= Align.ALIGNMENT_BOTTOM;
		mAlignment &= ~Align.ALIGNMENT_TOP;
	}
	
	public void north() {
		mAlignment = Align.ALIGNMENT_TOP;
	}

	public void west() {
		mAlignment = Align.ALIGNMENT_LEFT;
	}
	
	public void east() {
		mAlignment = Align.ALIGNMENT_RIGHT;
	}
	
	public void south() {
		mAlignment = Align.ALIGNMENT_BOTTOM;
	}
	
	public void northWest() {
		mAlignment = Align.ALIGNMENT_TOP | ALIGNMENT_LEFT;
	}
	
	public void northEast() {
		mAlignment = Align.ALIGNMENT_TOP | ALIGNMENT_RIGHT;
	}
	
	public void southWest() {
		mAlignment = Align.ALIGNMENT_BOTTOM | ALIGNMENT_LEFT;
	}
	
	public void southEast() {
		mAlignment = Align.ALIGNMENT_BOTTOM | ALIGNMENT_RIGHT;
	}

	public boolean isCenter() {
		return mAlignment == Align.ALIGNMENT_CENTER;
	}
	
	public boolean isTop() {
		return (mAlignment & Align.ALIGNMENT_TOP) != 0;
	}

	public boolean isLeft() {
		return (mAlignment & Align.ALIGNMENT_LEFT) != 0;
	}
	
	public boolean isRight() {
		return (mAlignment & Align.ALIGNMENT_RIGHT) != 0;
	}
	
	public boolean isBottom() {
		return (mAlignment & Align.ALIGNMENT_BOTTOM) != 0;
	}
	
	public boolean isNorth() {
		return (~mAlignment | Align.ALIGNMENT_TOP) == 0xFFFFFFFF;
	}

	public boolean isWest() {
		return (~mAlignment | Align.ALIGNMENT_LEFT) == 0xFFFFFFFF;
	}
	
	public boolean isEast() {
		return (~mAlignment | Align.ALIGNMENT_RIGHT) == 0xFFFFFFFF;
	}
	
	public boolean isSouth() {
		return (~mAlignment | Align.ALIGNMENT_BOTTOM) == 0xFFFFFFFF;
	}
	
	public boolean isNorthWest() {
		return isNorth() && isWest();
	}
	
	public boolean isNorthEast() {
		return  isNorth() && isEast();
	}
	
	public boolean isSouthWest() {
		return  isSouth() && isWest();
	}
	
	public boolean isSouthEast() {
		return isSouth() && isEast();
	}
	
	public HAlign getHAlign() {
		if(isLeft()) return HAlign.LEFT;
		if(isRight()) return HAlign.RIGHT;
		return HAlign.CENTER;
	}
	
	public VAlign getVAlign() {
		if(isTop()) return VAlign.TOP;
		if(isBottom()) return VAlign.BOTTOM;
		return VAlign.CENTER;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		Align other = (Align) obj;
		if(mAlignment != other.mAlignment) return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Align [");
		builder.append("HAlign=").append(getHAlign()).append(", ");
		builder.append("VAlign=").append(getVAlign());
		builder.append("]");
		return builder.toString();
	}
	
	private static class ImmutableAlign extends Align {
		
		public ImmutableAlign(int alignment) {
			super(alignment);
		}
		
		@Override
		public void set(Align align) {
			throw new UnsupportedOperationException("This instance is immutable");
		}
		
		@Override
		public void center() {
			throw new UnsupportedOperationException("This instance is immutable");
		}
		
		@Override
		public void left() {
			throw new UnsupportedOperationException("This instance is immutable");
		}
		
		@Override
		public void right() {
			throw new UnsupportedOperationException("This instance is immutable");
		}
		
		@Override
		public void top() {
			throw new UnsupportedOperationException("This instance is immutable");
		}
		
		@Override
		public void bottom() {
			throw new UnsupportedOperationException("This instance is immutable");
		}
		
		@Override
		public void west() {
			throw new UnsupportedOperationException("This instance is immutable");
		}
		
		@Override
		public void east() {
			throw new UnsupportedOperationException("This instance is immutable");
		}
		
		@Override
		public void north() {
			throw new UnsupportedOperationException("This instance is immutable");
		}
		
		@Override
		public void south() {
			throw new UnsupportedOperationException("This instance is immutable");
		}
	}
	
	public static interface Alignable<T> {
		
		public T center();
		
		public T top();

		public T left();
		
		public T right();
		
		public T bottom();
		
		public T north();

		public T west();
		
		public T east();
		
		public T south();
		
		public T northWest();
		
		public T northEast();
		
		public T southWest();
		
		public T southEast();
		
		public Align getAlign();
		
		public T setAlign(Align align);
	}
	
	/** 한 점을 기준으로 정렬한다. */
	public static interface PointAlignable<T> extends Alignable<T> {
	}
	
	/** 한 사각영역 내에서 정렬한다. */
	public static interface RectangleAlignable<T> extends Alignable<T> {
	}

}
