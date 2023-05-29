package app.nasagallery.common

class ConnectionError() : IllegalStateException("No connection")
class InternalError() : IllegalStateException("Data fetch internal error")
