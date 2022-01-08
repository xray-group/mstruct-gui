package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.core.AppContext;

public enum JobType {

	DATA_REFINEMENT("Data Refinement") {
		@Override
		public OptimizationJob createJob(AppContext context) {
			return new RefinementJob(context);
		}
	},
	GRID_REFINEMENT("Grid Refinement") {
		@Override
		public OptimizationJob createJob(AppContext context) {
			return new RefinementJob(context);
		}
	},
	MOCK_REFINEMENT_TEST("_mock Refinement TEST") {
		@Override
		public OptimizationJob createJob(AppContext context) {
			return new RefinmentTestJob(context);
		}
	},
	PING_TEST_10("Ping 10 TEST") {
		@Override
		public OptimizationJob createJob(AppContext context) {
			return new PingTestJob(context, 10);
		}
	},
	PING_TEST_FAIL("Ping 3 TEST") {
		@Override
		public OptimizationJob createJob(AppContext context) {
			return new PingTestJob(context, 3, true);
		}
	};

	private String name;

	JobType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	abstract public OptimizationJob createJob(AppContext context);

}
