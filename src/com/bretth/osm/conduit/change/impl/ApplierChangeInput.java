package com.bretth.osm.conduit.change.impl;

import com.bretth.osm.conduit.ConduitRuntimeException;
import com.bretth.osm.conduit.data.Node;
import com.bretth.osm.conduit.data.Element;
import com.bretth.osm.conduit.data.Segment;
import com.bretth.osm.conduit.data.Way;
import com.bretth.osm.conduit.task.ChangeAction;
import com.bretth.osm.conduit.task.ChangeSink;


/**
 * Receives input data for the "change" input to the change application.
 * 
 * @author Brett Henderson
 */
public class ApplierChangeInput extends ApplierInput implements ChangeSink {
	
	/**
	 * Creates a new instance.
	 * 
	 * @param sharedInputState
	 *            The shared state between input sources.
	 */
	public ApplierChangeInput(ApplierState sharedInputState) {
		super(sharedInputState);
	}
	

	/**
	 * {@inheritDoc}
	 */
	public void processNode(Node node, ChangeAction action) {
		sharedInputState.lock.lock();

		try {
			ComparisonOutcome comparisonOutcome;
			
			// Ensure the new processing state and data are valid.
			validateState(
				sharedInputState.changeStatus,
				InputStatus.NODE_STAGE,
				sharedInputState.lastChangeNode,
				node
			);
			
			// Update the state to match the new data.
			sharedInputState.changeStatus = InputStatus.NODE_STAGE;
			sharedInputState.lastChangeNode = node;
			
			// Notify the other source that new data is available.
			sharedInputState.lockCondition.signal();
			
			// Perform the comparison.
			comparisonOutcome = performElementComparison(
				sharedInputState.lockCondition,
				new InputState() {
					public InputStatus getThisSourceStatus() {
						return sharedInputState.changeStatus;
					}				
					public Element getThisSourceElement() {
						return sharedInputState.lastChangeNode;
					}
					public InputStatus getComparisonSourceStatus() {
						return sharedInputState.baseStatus;
					}
					public Element getComparisonSourceElement() {
						return sharedInputState.lastBaseNode;
					}
					public void checkForErrors() {
						validateNoErrors();
					}
				},
				true
			);
			
			// The "base" source only cares about elements that don't exist in
			// the "change" source (ie. the unchanged elements), the "change"
			// source performs all other updates.
			if (comparisonOutcome == ComparisonOutcome.DifferentElement) {
				// This element doesn't exist in the "base" source therefore we
				// are expecting an add.
				if (action.equals(ChangeAction.Create)) {
					sharedInputState.sink.processNode(sharedInputState.lastChangeNode);
					
				} else {
					throw new ConduitRuntimeException(
						"Cannot perform action " + action + " on node with id="
						+ sharedInputState.lastChangeNode.getId()
						+ " because it doesn't exist in the base source."
					);
				}
				
			} else if (comparisonOutcome == ComparisonOutcome.SameElement) {
				// The same element exists in both sources therefore we are
				// expecting a modify or delete.
				if (action.equals(ChangeAction.Modify)) {
					sharedInputState.sink.processNode(sharedInputState.lastChangeNode);
					
				} else if (action.equals(ChangeAction.Delete)) {
					// We don't need to do anything for delete.
					
				} else {
					throw new ConduitRuntimeException(
						"Cannot perform action " + action + " on node with id="
						+ sharedInputState.lastChangeNode.getId()
						+ " because it exists in the base source."
					);
				}
			}
			
		} finally {
			sharedInputState.lock.unlock();
		}
	}


	/**
	 * {@inheritDoc}
	 */
	public void processSegment(Segment segment, ChangeAction action) {
		sharedInputState.lock.lock();

		try {
			ComparisonOutcome comparisonOutcome;
			
			// Ensure the new processing state and data are valid.
			validateState(
				sharedInputState.changeStatus,
				InputStatus.SEGMENT_STAGE,
				sharedInputState.lastChangeSegment,
				segment
			);
			
			// Update the state to match the new data.
			sharedInputState.changeStatus = InputStatus.SEGMENT_STAGE;
			sharedInputState.lastChangeSegment = segment;
			
			// Notify the other source that new data is available.
			sharedInputState.lockCondition.signal();
			
			// Perform the comparison.
			comparisonOutcome = performElementComparison(
				sharedInputState.lockCondition,
				new InputState() {
					public InputStatus getThisSourceStatus() {
						return sharedInputState.changeStatus;
					}				
					public Element getThisSourceElement() {
						return sharedInputState.lastChangeSegment;
					}
					public InputStatus getComparisonSourceStatus() {
						return sharedInputState.baseStatus;
					}
					public Element getComparisonSourceElement() {
						return sharedInputState.lastBaseSegment;
					}
					public void checkForErrors() {
						validateNoErrors();
					}
				},
				true
			);
			
			// The "base" source only cares about elements that don't exist in
			// the "change" source (ie. the unchanged elements), the "change"
			// source performs all other updates.
			if (comparisonOutcome == ComparisonOutcome.DifferentElement) {
				// This element doesn't exist in the "base" source therefore we
				// are expecting an add.
				if (action.equals(ChangeAction.Create)) {
					sharedInputState.sink.processSegment(sharedInputState.lastChangeSegment);
					
				} else {
					throw new ConduitRuntimeException(
						"Cannot perform action " + action + " on segment with id="
						+ sharedInputState.lastChangeSegment.getId()
						+ " because it doesn't exist in the base source."
					);
				}
				
			} else if (comparisonOutcome == ComparisonOutcome.SameElement) {
				// The same element exists in both sources therefore we are
				// expecting a modify or delete.
				if (action.equals(ChangeAction.Modify)) {
					sharedInputState.sink.processSegment(sharedInputState.lastChangeSegment);
					
				} else if (action.equals(ChangeAction.Delete)) {
					// We don't need to do anything for delete.
					
				} else {
					throw new ConduitRuntimeException(
						"Cannot perform action " + action + " on segment with id="
						+ sharedInputState.lastChangeSegment.getId()
						+ " because it exists in the base source."
					);
				}
			}
			
		} finally {
			sharedInputState.lock.unlock();
		}
	}


	/**
	 * {@inheritDoc}
	 */
	public void processWay(Way way, ChangeAction action) {
		sharedInputState.lock.lock();

		try {
			ComparisonOutcome comparisonOutcome;
			
			// Ensure the new processing state and data are valid.
			validateState(
				sharedInputState.changeStatus,
				InputStatus.WAY_STAGE,
				sharedInputState.lastChangeWay,
				way
			);
			
			// Update the state to match the new data.
			sharedInputState.changeStatus = InputStatus.WAY_STAGE;
			sharedInputState.lastChangeWay = way;
			
			// Notify the other source that new data is available.
			sharedInputState.lockCondition.signal();
			
			// Perform the comparison.
			comparisonOutcome = performElementComparison(
				sharedInputState.lockCondition,
				new InputState() {
					public InputStatus getThisSourceStatus() {
						return sharedInputState.changeStatus;
					}				
					public Element getThisSourceElement() {
						return sharedInputState.lastChangeWay;
					}
					public InputStatus getComparisonSourceStatus() {
						return sharedInputState.baseStatus;
					}
					public Element getComparisonSourceElement() {
						return sharedInputState.lastBaseWay;
					}
					public void checkForErrors() {
						validateNoErrors();
					}
				},
				true
			);
			
			// The "base" source only cares about elements that don't exist in
			// the "change" source (ie. the unchanged elements), the "change"
			// source performs all other updates.
			if (comparisonOutcome == ComparisonOutcome.DifferentElement) {
				// This element doesn't exist in the "base" source therefore we
				// are expecting an add.
				if (action.equals(ChangeAction.Create)) {
					sharedInputState.sink.processWay(sharedInputState.lastChangeWay);
					
				} else {
					throw new ConduitRuntimeException(
						"Cannot perform action " + action + " on way with id="
						+ sharedInputState.lastChangeWay.getId()
						+ " because it doesn't exist in the base source."
					);
				}
				
			} else if (comparisonOutcome == ComparisonOutcome.SameElement) {
				// The same element exists in both sources therefore we are
				// expecting a modify or delete.
				if (action.equals(ChangeAction.Modify)) {
					sharedInputState.sink.processWay(sharedInputState.lastChangeWay);
					
				} else if (action.equals(ChangeAction.Delete)) {
					// We don't need to do anything for delete.
					
				} else {
					throw new ConduitRuntimeException(
						"Cannot perform action " + action + " on way with id="
						+ sharedInputState.lastChangeWay.getId()
						+ " because it exists in the base source."
					);
				}
			}
			
		} finally {
			sharedInputState.lock.unlock();
		}
	}

	
	/**
	 * Flags this source as complete. If both sources are complete, a complete
	 * request is called on the change sink.
	 */
	public void complete() {
		sharedInputState.lock.lock();

		try {
			// Ensure no errors have occurred.
			validateNoErrors();
			
			if (sharedInputState.changeStatus.compareTo(InputStatus.COMPLETE) < 0) {
				if (sharedInputState.baseStatus.compareTo(InputStatus.COMPLETE) >= 0) {
					sharedInputState.sink.complete();
				}
				
				sharedInputState.changeStatus = InputStatus.COMPLETE;
			}
			
			// Notify the other source that new data is available.
			sharedInputState.lockCondition.signal();
			
		} finally {
			sharedInputState.lock.unlock();
		}
	}


	/**
	 * Flags this source as released. If both sources are released, a release
	 * request is called on the change sink.
	 */
	public void release() {
		sharedInputState.lock.lock();

		try {
			if (!sharedInputState.changeReleased) {
				if (sharedInputState.baseReleased) {
					sharedInputState.sink.release();
				}

				sharedInputState.changeReleased = true;
			}
			
			// Notify the other source that new data is available.
			sharedInputState.lockCondition.signal();
			
		} finally {
			sharedInputState.lock.unlock();
		}
	}
}