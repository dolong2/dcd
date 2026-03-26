package com.dcd.server.core.common.service.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class BloomFilterReservationException : BasicException(ErrorCode.FAILURE_BLOOM_FILTER_RESERVATION)