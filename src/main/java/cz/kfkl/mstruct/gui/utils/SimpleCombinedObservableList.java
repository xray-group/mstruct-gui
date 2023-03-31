package cz.kfkl.mstruct.gui.utils;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.WeakListChangeListener;

/**
 * Combines multiple "source" lists into one {@link ObservableList}. Source
 * lists which are {@link ObservableList}s are listen to and any list change
 * event is propagated to this list.
 */
public class SimpleCombinedObservableList<E> extends ObservableListBase<E> {

	private List<CombinedListSource> sources = new ArrayList<>();

	private List<E> target = new ArrayList<>();

	private IntegerProperty sizeProperty = new SimpleIntegerProperty(0);

	/**
	 * @param sourceLists the wrapped lists
	 */
	@SuppressWarnings("unchecked")
	public SimpleCombinedObservableList(List<? extends E>... sourceLists) {

		if (sourceLists == null) {
			throw new NullPointerException("Source list is not specified");
		}

		SimpleCombinedObservableList<E>.CombinedListSource previousSource = null;
		for (List<? extends E> srcList : sourceLists) {
			previousSource = registerSourceList(srcList, previousSource);
		}

		if (previousSource != null) {
			sizeProperty.bind(previousSource.endIndex());
		}
	}

	public void addList(List<? extends E> srcList) {
		SimpleCombinedObservableList<E>.CombinedListSource lastSource = findLastSource();
		lastSource = registerSourceList(srcList, lastSource);

		sizeProperty.bind(lastSource.endIndex());
	}

	private SimpleCombinedObservableList<E>.CombinedListSource findLastSource() {
		return this.sources.isEmpty() ? null : this.sources.get(sources.size() - 1);
	}

	private <S> SimpleCombinedObservableList<E>.CombinedListSource registerSourceList(List<? extends E> srcList,
			SimpleCombinedObservableList<E>.CombinedListSource previousSource) {
		SimpleCombinedObservableList<E>.CombinedListSource cls = new CombinedListSource(srcList, previousSource);
		this.sources.add(cls);

		beginChange();
		for (E srcEl : srcList) {
			target.add(srcEl);
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

	private class CombinedListSource {

		private NumberBinding start;
		private IntegerProperty size = new SimpleIntegerProperty();
		CombinedListChangeListener listener;

		public CombinedListSource(List<? extends E> srcList, CombinedListSource previousSource) {
			if (previousSource != null) {
				this.start = previousSource.endIndex();
			}
			this.size = new SimpleIntegerProperty(srcList.size());

			if (srcList instanceof ObservableList) {
				listener = new CombinedListChangeListener(this);
				((ObservableList<? extends E>) srcList).addListener(new WeakListChangeListener<>(listener));
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

	private class CombinedListChangeListener implements ListChangeListener<E> {

		private SimpleCombinedObservableList<E>.CombinedListSource combinedListSource;

		public CombinedListChangeListener(SimpleCombinedObservableList<E>.CombinedListSource combinedListSource) {
			this.combinedListSource = combinedListSource;
		}

		@Override
		public void onChanged(Change<? extends E> c) {
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

		private void permutate(Change<? extends E> c) {
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

		private void update(Change<? extends E> c) {
			int to = c.getTo();
			int start = getTargetStart();

			for (int i = c.getFrom(); i < to; i++) {
				nextUpdate(start + i);
			}
		}

		private void addRemove(Change<? extends E> c) {
			int from = c.getFrom();
			int to = c.getTo();
			int start = getTargetStart();

			if (c.wasRemoved()) {
				for (int i = c.getRemovedSize() - 1; i >= 0; i--) {
					E removed = target.remove(start + from + i);
					nextRemove(start + from + i, removed);
				}
			}

			if (c.wasAdded()) {
				if (from < to) {
					ArrayList<E> added = new ArrayList<>();
					for (int i = from; i < to; i++) {
						E addedEl = c.getList().get(i);
						added.add(addedEl);
					}

					target.addAll(start + from, added);
					nextAdd(start + from, start + to);
				}
			}

			updateSize(c.getList().size());
		}

		private int getTargetStart() {
			return combinedListSource.getStartIndex();
		}

		private void updateSize(int newSize) {
			combinedListSource.updateSize(newSize);
		}
	}
}
