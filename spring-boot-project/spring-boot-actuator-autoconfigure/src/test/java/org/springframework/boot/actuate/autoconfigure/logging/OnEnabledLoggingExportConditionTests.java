/*
 * Copyright 2012-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.actuate.autoconfigure.logging;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link OnEnabledLoggingExportCondition}.
 *
 * @author Moritz Halbritter
 * @author Dmytro Nosan
 */
class OnEnabledLoggingExportConditionTests {

	@Test
	void shouldMatchIfNoPropertyIsSet() {
		OnEnabledLoggingExportCondition condition = new OnEnabledLoggingExportCondition();
		ConditionOutcome outcome = condition.getMatchOutcome(mockConditionContext(), mockMetadata(""));
		assertThat(outcome.isMatch()).isTrue();
		assertThat(outcome.getMessage()).isEqualTo("@ConditionalOnEnabledLoggingExport is enabled by default");
	}

	@Test
	void shouldNotMatchIfGlobalPropertyIsFalse() {
		OnEnabledLoggingExportCondition condition = new OnEnabledLoggingExportCondition();
		ConditionOutcome outcome = condition.getMatchOutcome(
				mockConditionContext(Map.of("management.logging.export.enabled", "false")), mockMetadata(""));
		assertThat(outcome.isMatch()).isFalse();
		assertThat(outcome.getMessage())
			.isEqualTo("@ConditionalOnEnabledLoggingExport management.logging.export.enabled is false");
	}

	@Test
	void shouldMatchIfGlobalPropertyIsTrue() {
		OnEnabledLoggingExportCondition condition = new OnEnabledLoggingExportCondition();
		ConditionOutcome outcome = condition.getMatchOutcome(
				mockConditionContext(Map.of("management.logging.export.enabled", "true")), mockMetadata(""));
		assertThat(outcome.isMatch()).isTrue();
		assertThat(outcome.getMessage())
			.isEqualTo("@ConditionalOnEnabledLoggingExport management.logging.export.enabled is true");
	}

	@Test
	void shouldNotMatchIfExporterPropertyIsFalse() {
		OnEnabledLoggingExportCondition condition = new OnEnabledLoggingExportCondition();
		ConditionOutcome outcome = condition.getMatchOutcome(
				mockConditionContext(Map.of("management.otlp.logging.export.enabled", "false")), mockMetadata("otlp"));
		assertThat(outcome.isMatch()).isFalse();
		assertThat(outcome.getMessage())
			.isEqualTo("@ConditionalOnEnabledLoggingExport management.otlp.logging.export.enabled is false");
	}

	@Test
	void shouldMatchIfExporterPropertyIsTrue() {
		OnEnabledLoggingExportCondition condition = new OnEnabledLoggingExportCondition();
		ConditionOutcome outcome = condition.getMatchOutcome(
				mockConditionContext(Map.of("management.otlp.logging.export.enabled", "true")), mockMetadata("otlp"));
		assertThat(outcome.isMatch()).isTrue();
		assertThat(outcome.getMessage())
			.isEqualTo("@ConditionalOnEnabledLoggingExport management.otlp.logging.export.enabled is true");
	}

	@Test
	void exporterPropertyShouldOverrideGlobalPropertyIfTrue() {
		OnEnabledLoggingExportCondition condition = new OnEnabledLoggingExportCondition();
		ConditionOutcome outcome = condition.getMatchOutcome(mockConditionContext(
				Map.of("management.logging.enabled", "false", "management.otlp.logging.export.enabled", "true")),
				mockMetadata("otlp"));
		assertThat(outcome.isMatch()).isTrue();
		assertThat(outcome.getMessage())
			.isEqualTo("@ConditionalOnEnabledLoggingExport management.otlp.logging.export.enabled is true");
	}

	@Test
	void exporterPropertyShouldOverrideGlobalPropertyIfFalse() {
		OnEnabledLoggingExportCondition condition = new OnEnabledLoggingExportCondition();
		ConditionOutcome outcome = condition.getMatchOutcome(mockConditionContext(
				Map.of("management.logging.enabled", "true", "management.otlp.logging.export.enabled", "false")),
				mockMetadata("otlp"));
		assertThat(outcome.isMatch()).isFalse();
		assertThat(outcome.getMessage())
			.isEqualTo("@ConditionalOnEnabledLoggingExport management.otlp.logging.export.enabled is false");
	}

	private ConditionContext mockConditionContext() {
		return mockConditionContext(Collections.emptyMap());
	}

	private ConditionContext mockConditionContext(Map<String, String> properties) {
		ConditionContext context = mock(ConditionContext.class);
		MockEnvironment environment = new MockEnvironment();
		properties.forEach(environment::setProperty);
		given(context.getEnvironment()).willReturn(environment);
		return context;
	}

	private AnnotatedTypeMetadata mockMetadata(String exporter) {
		AnnotatedTypeMetadata metadata = mock(AnnotatedTypeMetadata.class);
		given(metadata.getAnnotationAttributes(ConditionalOnEnabledLoggingExport.class.getName()))
			.willReturn(Map.of("value", exporter));
		return metadata;
	}

}
