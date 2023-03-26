package cz.kfkl.mstruct.gui.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.WeakListChangeListener;
import javafx.collections.transformation.TransformationList;

/**
 * Inspired by the {@link TransformationList} but extended to be able to wrap
 * multiple source lists.
 */
public class CombinedObservableList<F, E> extends ObservableListBase<E> implements ObservableList<E> {

	private List<CombinedListSource> sources = new ArrayList<>();

	private List<E> target;

	private Function<F, E> commonMapper;

	private IntegerProperty sizeProperty = new SimpleIntegerProperty(0);

	public CombinedObservableList(Function<F, E> mapper) {

		if (mapper == null) {
			throw new NullPointerException("Mapper function must be specified.");
		}

		this.commonMapper = mapper;
		this.target = new ArrayList<>();
	}

	/**
	 * @param sourceLists the wrapped lists
	 */
	@SuppressWarnings("unchecked")
	public CombinedObservableList(Function<F, E> mapper, List<? extends F>... sourceLists) {

		this(mapper);

		if (sourceLists == null) {
			throw new NullPointerException("Source list is not specified");
		}

		CombinedObservableList<F, E>.CombinedListSource<F> previousSource = null;
		for (List<? extends F> srcList : sourceLists) {
			previousSource = registerSourceList(mapper, srcList, previousSource);
		}

		if (previousSource != null) {
			sizeProperty.bind(previousSource.endIndex());
		}
	}

	public void addList(List<? extends F> srcList) {
		addList(commonMapper, srcList);
	}

	public <S> void addList(Function<S, E> mapper, List<? extends S> srcList) {
		CombinedObservableList.CombinedListSource lastSource = findLastSource();
		lastSource = registerSourceList(mapper, srcList, lastSource);

		sizeProperty.bind(lastSource.endIndex());
	}

	private CombinedObservableList.CombinedListSource findLastSource() {
		return this.sources.isEmpty() ? null : this.sources.get(sources.size() - 1);
	}

	private <S> CombinedObservableList<S, E>.CombinedListSource<S> registerSourceList(Function<S, E> mapper,
			List<? extends S> srcList, CombinedObservableList<S, E>.CombinedListSource<S> previousSource) {
		CombinedObservableList<S, E>.CombinedListSource<S> cls = new CombinedListSource(srcList, mapper, previousSource);
		this.sources.add(cls);

		beginChange();
		for (S srcEl : srcList) {
			target.add(mapper.apply(srcEl));
		}
		int start = cls.getStartIndex();
		int end = cls.endIndex().intValue();
		nextAdd(start, end);
		endChange();
		return cls;
	}

	@Override
	public E get(int index) {
		return target.get(index);
	}

	@Override
	public int size() {
		return target.size();
	}

	private class CombinedListSource<S> {

		private NumberBinding start;
		private IntegerProperty size = new SimpleIntegerProperty();
		CombinedListChangeListener<S> listener;
		private List<S> srcList;

		public CombinedListSource(List<S> srcList, Function<S, E> mapper, CombinedListSource<S> previousSource) {
			this.srcList = srcList;
			if (previousSource != null) {
				this.start = previousSource.endIndex();
			}
			this.size = new SimpleIntegerProperty(srcList.size());

			if (srcList instanceof ObservableList) {
				listener = new CombinedListChangeListener(mapper, this);
				((ObservableList<? extends S>) srcList).addListener(new WeakListChangeListener<>(listener));
			}

		}

		NumberBinding endIndex() {
			NumberBinding add;
			if (start == null) {
				add = size.add(0);
			} else {
				add = size.add(start);
			}
			return add;
		}

		private int getStartIndex() {
			return start == null ? 0 : start.intValue();
		}

		private void updateSize(int newSize) {
			size.set(newSize);
		}
	}

	private class CombinedListChangeListener<S> implements ListChangeListener<S> {

		private Function<S, E> mapper;
		private CombinedObservableList<S, E>.CombinedListSource<S> combinedListSource;

		public CombinedListChangeListener(Function<S, E> mapper,
				CombinedObservableList<S, E>.CombinedListSource<S> combinedListSource) {
			this.mapper = mapper;
			this.combinedListSource = combinedListSource;
		}

		@Override
		public void onChanged(Change<? extends S> c) {
			beginChange();
			while (c.next()) {
				if (c.wasPermutated()) {
					permutate(c);
				} else if (c.wasUpdated()) {
					update(c);
				} else {
					addRemove(c);
				}
			}
			endChange();
		}

		private void permutate(Change<? extends S> c) {
			int from = c.getFrom();
			int to = c.getTo();

			if (to > from) {
				int targetListStart = getTargetStart();

				int[] perm = new int[to - from];
				ArrayList<E> newTarget = new ArrayList<>(target);

				for (int i = from; i < to; i++) {
					int newSrcPosition = c.getPermutation(i);
					int newTargetPosition = targetListStart + newSrcPosition;
					perm[i - from] = newTargetPosition;
					newTarget.set(newTargetPosition, target.get(targetListStart + i));
				}

				nextPermutation(targetListStart + from, targetListStart + to, perm);
				target = newTarget;
			}
		}

		private void update(Change<? extends S> c) {
			int to = c.getTo();
			int start = getTargetStart();

			for (int i = c.getFrom(); i < to; i++) {
				nextUpdate(start + i);
			}
		}

		private void addRemove(Change<? extends S> c) {
			int from = c.getFrom();
			int to = c.getTo();
			int start = getTargetStart();

//			for (int i = 0, sz = c.getRemovedSize(); i < sz; ++i) {
//				nextRemove(from, target.get(c.getFrom() + i));
//			}
			for (int i = c.getRemovedSize() - 1; i >= 0; i--) {
				E removed = target.remove(start + from + i);
				nextRemove(start + from + i, removed);
			}

			if (from < to) {
				ArrayList<E> added = new ArrayList<>();
				for (int i = from; i < to; i++) {
					S addedEl = c.getList().get(i);
					E addedElWrapped = mapper.apply(addedEl);
					added.add(addedElWrapped);
				}

				target.addAll(start + from, added);
				nextAdd(start + from, start + to);
			}

			updateSize(c.getList().size());

			// nextReplace(from, to, removed);
		}

		private int getTargetStart() {
			return combinedListSource.getStartIndex();
		}

		private void updateSize(int newSize) {
			combinedListSource.updateSize(newSize);
		}

	}

}
