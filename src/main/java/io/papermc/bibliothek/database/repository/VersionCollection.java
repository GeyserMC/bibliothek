/*
 * This file is part of bibliothek, licensed under the MIT License.
 *
 * Copyright (c) 2019-2023 PaperMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.papermc.bibliothek.database.repository;

import io.papermc.bibliothek.database.model.Version;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionCollection extends MongoRepository<Version, ObjectId> {
  List<Version> findAllByProject(final ObjectId project);

  List<Version> findAllByProjectAndGroup(final ObjectId project, final ObjectId group);

  Optional<Version> findByProjectAndName(final ObjectId project, final String name);

  @Query("{project: { $eq: ?0 }}")
  @Aggregation({"{ $sort: { time: -1 }}", "{ $limit: 1 }"})
  Version findLatestVersion(final ObjectId project);

  default Optional<Version> findCorrectVersion(final ObjectId project, final String name) {
    if ("latest".equals(name)) {
      // when project exists, it has to have at least one version
      return Optional.of(findLatestVersion(project));
    }
    return findByProjectAndName(project, name);
  }
}
