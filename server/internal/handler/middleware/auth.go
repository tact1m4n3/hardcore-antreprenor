package middleware

import (
	"net/http"
	"prohiking-server/internal/auth"
	"prohiking-server/internal/response"
)

func Auth(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if cookie, err := r.Cookie("jwt"); err == nil {
			if token, err := auth.ParseJWT(cookie.Value); err == nil && token.Valid {
				next.ServeHTTP(w, r)
				return
			}
		}
		response.Error(w, http.StatusUnauthorized, "you are not logged in")
	})
}
