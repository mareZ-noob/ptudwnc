# Rotation strategy
# Daily rotation
output {
  # Send the parsed log data to our Elasticsearch instance
  elasticsearch {
    hosts => ["http://elasticsearch:9200"]
    index => "webnc-logs-%{+YYYY.MM.dd}" # Daily index rotation
  }

  stdout {
    codec => rubydebug
  }
}

# Weekly rotation
output {
  # Send the parsed log data to our Elasticsearch instance
  elasticsearch {
    hosts => ["http://elasticsearch:9200"]
    # Use a weekly pattern
    index => "webnc-logs-%{+YYYY.ww}"
  }

  stdout {
    codec => rubydebug
  }
}

# Size rotation
output {
  elasticsearch {
    hosts => ["http://elasticsearch:9200"]
    # Always write to the alias, not a time-based index
    index => "webnc-logs-write"
  }

  stdout {
    codec => rubydebug
  }
}


# Time range rotation (e.g., Daily rotation, Weekly rotation,...)
# Create the ilm policy
PUT _ilm/policy/delete_logs_after_1_min
{
  "policy": {
    "phases": {
      "hot": {
        "min_age": "1m",
        "actions": {}
      },
      "delete": {
        "min_age": "0ms",
        "actions": {
          "delete": {}
        }
      }
    }
  }
}
# The result will look like this:
{
  "acknowledged": true
}

# Create the index template for future indices
PUT _index_template/webnc_logs_template
{
  "index_patterns": ["webnc-logs-*"],
  "template": {
    "settings": {
      "index.lifecycle.name": "delete_logs_after_1_min"
    }
  }
}
# The result will look like this:
{
  "acknowledged": true
}

# Apply the policy to all existing indices
PUT /webnc-logs-*/_settings
{
  "index": {
    "lifecycle": {
      "name": "delete_logs_after_1_min"
    }
  }
}
# The result will look like this:
{
  "acknowledged": true
}

# Check Detailed status for all indices matching the pattern.
GET /webnc-logs-*/_ilm/explain
# The result will look like this:
{
  "indices": {
    "webnc-logs-2025.10.16": {
      "index": "webnc-logs-2025.10.16",
      "managed": true,
      "policy": "delete_logs_after_1_min",
      "index_creation_date_millis": 1760599162144,
      "time_since_index_creation": "17.02s",
      "lifecycle_date_millis": 1760599162144,
      "age": "17.02s",
      "phase": "new",
      "phase_time_millis": 1760599162313,
      "action": "complete",
      "action_time_millis": 1760599162313,
      "step": "complete",
      "step_time_millis": 1760599162313,
      "phase_execution": {
        "policy": "delete_logs_after_1_min",
        "version": 1,
        "modified_date_in_millis": 1760597211281
      }
    }
  }
}

# Check the settings of all matching indices to see if the lifecycle name is present.
GET /webnc-logs-*/_settings
# The result will look like this:
{
  "webnc-logs-2025.10.16": {
    "settings": {
      "index": {
        "lifecycle": {
          "name": "delete_logs_after_1_min"
        },
        "routing": {
          "allocation": {
            "include": {
              "_tier_preference": "data_content"
            }
          }
        },
        "number_of_shards": "1",
        "provided_name": "webnc-logs-2025.10.16",
        "creation_date": "1760599162144",
        "number_of_replicas": "1",
        "uuid": "Ll_XbrjMROmWrX450M59Qw",
        "version": {
          "created": "8090099"
        }
      }
    }
  }
}

# Verify that the index template is correctly configured.
GET /_index_template/webnc_logs_template
# The result will look like this:
{
  "index_templates": [
    {
      "name": "webnc_logs_template",
      "index_template": {
        "index_patterns": [
          "webnc-logs-*"
        ],
        "template": {
          "settings": {
            "index": {
              "lifecycle": {
                "name": "delete_logs_after_1_min"
              }
            }
          }
        },
        "composed_of": []
      }
    }
  ]
}

# Check the current cluster settings for the ILM poll_interval
GET _cluster/settings?include_defaults=true&filter_path=**.lifecycle.poll_interval
# The result will look like this:
{
  "defaults": {
    "indices": {
      "lifecycle": {
        "poll_interval": "10m"
      }
    }
  }
}

# Temporarily set the poll_interval to 10 seconds for faster testing
PUT _cluster/settings
{
  "transient": {
    "indices.lifecycle.poll_interval": "10s"
  }
}
# The result will look like this:
{
  "acknowledged": true,
  "persistent": {},
  "transient": {
    "indices": {
      "lifecycle": {
        "poll_interval": "10s"
      }
    }
  }
}

# Reset the poll_interval to its default value
PUT _cluster/settings
{
  "transient": {
    "indices.lifecycle.poll_interval": null
  }
}
# The result will look like this:
{
  "acknowledged": true,
  "persistent": {},
  "transient": {}
}

# Check
GET /_cat/indices/webnc-logs-*?v
# The result will look like this:
health status index                 uuid                   pri rep docs.count docs.deleted store.size pri.store.size
yellow open   webnc-logs-2025.10.16 WABMex1tShO-n9jD1OlWxw   1   1        858            0    269.2kb        269.2kb

# Remove the ILM policy from all indices matching the pattern
POST /webnc-logs-*/_ilm/remove
# The result will look like this:
{
  "has_failures": false,
  "failed_indexes": []
}

# Delete the index template to prevent the policy from being applied to new indices
DELETE /_index_template/webnc_logs_template
# The result will look like this:
{
  "acknowledged": true
}

# To confirm the policy has been removed
GET /webnc-logs-*/_ilm/explain
# The result will look like this:
{
  "indices": {
    "webnc-logs-2025.10.16": {
      "index": "webnc-logs-2025.10.16",
      "managed": false
    }
  }
}