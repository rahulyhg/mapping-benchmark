package org.simpleflatmapper.querydsl;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.query.sql.*;
import com.mysema.query.types.Projections;
import com.mysema.query.types.QBean;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.simpleflatmapper.beans.MappedObject16;
import org.simpleflatmapper.beans.MappedObject4;
import org.simpleflatmapper.db.ConnectionParam;
import org.simpleflatmapper.db.DbTarget;
import org.simpleflatmapper.param.LimitParam;
import org.simpleflatmapper.querydsl.beans.QTestBenchmarkObject16;
import org.simpleflatmapper.querydsl.beans.QTestSmallBenchmarkObject;


@State(Scope.Benchmark)
public class QueryDslBenchmark {

	public static final QBean<MappedObject4> MAPPED_OBJECT_4_Q_BEAN = Projections.bean(MappedObject4.class, QTestSmallBenchmarkObject.testSmallBenchmarkObject.all());
	public static final QBean<MappedObject16> MAPPED_OBJECT_16_Q_BEAN = Projections.bean(MappedObject16.class, QTestBenchmarkObject16.testBenchmarkObject16.all());

	@Param(value = "H2")
	private DbTarget db;

	private SQLTemplates templates;
	@Setup
	public void init() throws Exception {
		ConnectionParam cp = new ConnectionParam();
		cp.db = db;
		cp.init();

		templates = getSqlDialect(db);

	}

	public static SQLTemplates getSqlDialect(DbTarget db) {
		switch (db) {
			case MYSQL:
			case MOCK:
				return new MySQLTemplates();
			case H2:
				return new H2Templates();
			case HSQLDB:
				return new HSQLDBTemplates();
		}
		throw new IllegalArgumentException();

	}


	@Benchmark
	public void _04Fields(LimitParam limit, final Blackhole blackhole, ConnectionParam connectionParam) throws Exception {
		SQLQuery query = new SQLQuery(connectionParam.connection, templates);

		CloseableIterator<MappedObject4> iterate = query.from(QTestSmallBenchmarkObject.testSmallBenchmarkObject)
				.limit(limit.limit)
				.iterate(MAPPED_OBJECT_4_Q_BEAN);

		while(iterate.hasNext()) {
			blackhole.consume(iterate.next());
		}
	}


	@Benchmark
	public void _16Fields(LimitParam limit, final Blackhole blackhole, ConnectionParam connectionParam) throws Exception {
		SQLQuery query = new SQLQuery(connectionParam.connection, templates);

		CloseableIterator<MappedObject16> iterate = query.from(QTestBenchmarkObject16.testBenchmarkObject16)
				.limit(limit.limit)
				.iterate(MAPPED_OBJECT_16_Q_BEAN);

		while(iterate.hasNext()) {
			blackhole.consume(iterate.next());
		}
	}
}
