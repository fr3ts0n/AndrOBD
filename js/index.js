function toggleNavigation() {
    $("body").hasClass("is-expanded") ? ($("body").removeClass("is-expanded"), $(".menu-section-container-nav").css({
        display: "none",
        opacity: 0
    })) : ($("body").addClass("is-expanded"), $(".menu-section-container-nav").css({
        display: "flex"
    }), setTimeout(function() {
        $(".menu-section-container-nav").css({
            opacity: 1
        })
    }, 500)), $("#nav-toggle").hasClass("active") ? $("#nav-toggle").removeClass("active") : $("#nav-toggle").addClass("active")
}

! function(e, t, n) {
    function i(e, t, n) {
        return e.call.apply(e.bind, arguments)
    }

    function r(e, t, n) {
        if (!e) throw Error();
        if (2 < arguments.length) {
            var i = Array.prototype.slice.call(arguments, 2);
            return function() {
                var n = Array.prototype.slice.call(arguments);
                return Array.prototype.unshift.apply(n, i), e.apply(t, n)
            }
        }
        return function() {
            return e.apply(t, arguments)
        }
    }

    function o(e, t, n) {
        return o = Function.prototype.bind && -1 != Function.prototype.bind.toString().indexOf("native code") ? i : r, o.apply(null, arguments)
    }

    function s(e, t) {
        this.K = e, this.w = t || e, this.G = this.w.document
    }

    function a(e, n, i) {
        e = e.G.getElementsByTagName(n)[0], e || (e = t.documentElement), e && e.lastChild && e.insertBefore(i, e.lastChild)
    }

    function u(e, t) {
        function n() {
            e.G.body ? t() : setTimeout(n, 0)
        }
        n()
    }

    function l(e, t, n) {
        t = t || [], n = n || [];
        for (var i = e.className.split(/\s+/), r = 0; r < t.length; r += 1) {
            for (var o = !1, s = 0; s < i.length; s += 1)
                if (t[r] === i[s]) {
                    o = !0;
                    break
                }
            o || i.push(t[r])
        }
        for (t = [], r = 0; r < i.length; r += 1) {
            for (o = !1, s = 0; s < n.length; s += 1)
                if (i[r] === n[s]) {
                    o = !0;
                    break
                }
            o || t.push(i[r])
        }
        e.className = t.join(" ").replace(/\s+/g, " ").replace(/^\s+|\s+$/, "")
    }

    function c(e, t) {
        for (var n = e.className.split(/\s+/), i = 0, r = n.length; r > i; i++)
            if (n[i] == t) return !0;
        return !1
    }

    function f(e) {
        if ("string" == typeof e.na) return e.na;
        var t = e.w.location.protocol;
        return "about:" == t && (t = e.K.location.protocol), "https:" == t ? "https:" : "http:"
    }

    function d(e, t) {
        var n = e.createElement("link", {
                rel: "stylesheet",
                href: t,
                media: "all"
            }),
            i = !1;
        n.onload = function() {
            i || (i = !0)
        }, n.onerror = function() {
            i || (i = !0)
        }, a(e, "head", n)
    }

    function p(t, n, i, r) {
        var o = t.G.getElementsByTagName("head")[0];
        if (o) {
            var s = t.createElement("script", {
                    src: n
                }),
                a = !1;
            return s.onload = s.onreadystatechange = function() {
                a || this.readyState && "loaded" != this.readyState && "complete" != this.readyState || (a = !0, i && i(null), s.onload = s.onreadystatechange = null, "HEAD" == s.parentNode.tagName && o.removeChild(s))
            }, o.appendChild(s), e.setTimeout(function() {
                a || (a = !0, i && i(Error("Script load timeout")))
            }, r || 5e3), s
        }
        return null
    }

    function h(e, t) {
        this.Y = e, this.ga = t
    }

    function g(e, t, n, i) {
        this.c = null != e ? e : null, this.g = null != t ? t : null, this.D = null != n ? n : null, this.e = null != i ? i : null
    }

    function m(e) {
        e = Q.exec(e);
        var t = null,
            n = null,
            i = null,
            r = null;
        return e && (null !== e[1] && e[1] && (t = parseInt(e[1], 10)), null !== e[2] && e[2] && (n = parseInt(e[2], 10)), null !== e[3] && e[3] && (i = parseInt(e[3], 10)), null !== e[4] && e[4] && (r = /^[0-9]+$/.test(e[4]) ? parseInt(e[4], 10) : e[4])), new g(t, n, i, r)
    }

    function v(e, t, n, i, r, o, s, a) {
        this.N = e, this.k = a
    }

    function y(e) {
        this.a = e
    }

    function x(e) {
        var t = T(e.a, /(iPod|iPad|iPhone|Android|Windows Phone|BB\d{2}|BlackBerry)/, 1);
        return "" != t ? (/BB\d{2}/.test(t) && (t = "BlackBerry"), t) : (e = T(e.a, /(Linux|Mac_PowerPC|Macintosh|Windows|CrOS|PlayStation|CrKey)/, 1), "" != e ? ("Mac_PowerPC" == e ? e = "Macintosh" : "PlayStation" == e && (e = "Linux"), e) : "Unknown")
    }

    function w(e) {
        var t = T(e.a, /(OS X|Windows NT|Android) ([^;)]+)/, 2);
        if (t || (t = T(e.a, /Windows Phone( OS)? ([^;)]+)/, 2)) || (t = T(e.a, /(iPhone )?OS ([\d_]+)/, 2))) return t;
        if (t = T(e.a, /(?:Linux|CrOS|CrKey) ([^;)]+)/, 1))
            for (var t = t.split(/\s/), n = 0; n < t.length; n += 1)
                if (/^[\d\._]+$/.test(t[n])) return t[n];
        return (e = T(e.a, /(BB\d{2}|BlackBerry).*?Version\/([^\s]*)/, 2)) ? e : "Unknown"
    }

    function b(e) {
        var t = x(e),
            n = m(w(e)),
            i = m(T(e.a, /AppleWeb(?:K|k)it\/([\d\.\+]+)/, 1)),
            r = "Unknown",
            o = new g,
            o = "Unknown",
            s = !1;
        return /OPR\/[\d.]+/.test(e.a) ? r = "Opera" : -1 != e.a.indexOf("Chrome") || -1 != e.a.indexOf("CrMo") || -1 != e.a.indexOf("CriOS") ? r = "Chrome" : /Silk\/\d/.test(e.a) ? r = "Silk" : "BlackBerry" == t || "Android" == t ? r = "BuiltinBrowser" : -1 != e.a.indexOf("PhantomJS") ? r = "PhantomJS" : -1 != e.a.indexOf("Safari") ? r = "Safari" : -1 != e.a.indexOf("AdobeAIR") ? r = "AdobeAIR" : -1 != e.a.indexOf("PlayStation") && (r = "BuiltinBrowser"), "BuiltinBrowser" == r ? o = "Unknown" : "Silk" == r ? o = T(e.a, /Silk\/([\d\._]+)/, 1) : "Chrome" == r ? o = T(e.a, /(Chrome|CrMo|CriOS)\/([\d\.]+)/, 2) : -1 != e.a.indexOf("Version/") ? o = T(e.a, /Version\/([\d\.\w]+)/, 1) : "AdobeAIR" == r ? o = T(e.a, /AdobeAIR\/([\d\.]+)/, 1) : "Opera" == r ? o = T(e.a, /OPR\/([\d.]+)/, 1) : "PhantomJS" == r && (o = T(e.a, /PhantomJS\/([\d.]+)/, 1)), o = m(o), s = "AdobeAIR" == r ? 2 < o.c || 2 == o.c && 5 <= o.g : "BlackBerry" == t ? 10 <= n.c : "Android" == t ? 2 < n.c || 2 == n.c && 1 < n.g : 526 <= i.c || 525 <= i.c && 13 <= i.g, new v(r, 0, 0, 0, 0, 0, 0, new h(s, 536 > i.c || 536 == i.c && 11 > i.g))
    }

    function T(e, t, n) {
        return (e = e.match(t)) && e[n] ? e[n] : ""
    }

    function C(e) {
        this.ma = e || "-"
    }

    function k(e, t) {
        this.N = e, this.Z = 4, this.O = "n";
        var n = (t || "n4").match(/^([nio])([1-9])$/i);
        n && (this.O = n[1], this.Z = parseInt(n[2], 10))
    }

    function E(e) {
        return e.O + e.Z
    }

    function S(e) {
        var t = 4,
            n = "n",
            i = null;
        return e && ((i = e.match(/(normal|oblique|italic)/i)) && i[1] && (n = i[1].substr(0, 1).toLowerCase()), (i = e.match(/([1-9]00|normal|bold)/i)) && i[1] && (/bold/i.test(i[1]) ? t = 7 : /[1-9]00/.test(i[1]) && (t = parseInt(i[1].substr(0, 1), 10)))), n + t
    }

    function N(e, t) {
        this.d = e, this.q = e.w.document.documentElement, this.Q = t, this.j = "wf", this.h = new C("-"), this.ha = !1 !== t.events, this.F = !1 !== t.classes
    }

    function A(e) {
        if (e.F) {
            var t = c(e.q, e.h.e(e.j, "active")),
                n = [],
                i = [e.h.e(e.j, "loading")];
            t || n.push(e.h.e(e.j, "inactive")), l(e.q, n, i)
        }
        j(e, "inactive")
    }

    function j(e, t, n) {
        e.ha && e.Q[t] && (n ? e.Q[t](n.getName(), E(n)) : e.Q[t]())
    }

    function D() {
        this.C = {}
    }

    function L(e, t) {
        this.d = e, this.I = t, this.o = this.d.createElement("span", {
            "aria-hidden": "true"
        }, this.I)
    }

    function O(e, t) {
        var n, i = e.o;
        n = [];
        for (var r = t.N.split(/,\s*/), o = 0; o < r.length; o++) {
            var s = r[o].replace(/['"]/g, ""); - 1 == s.indexOf(" ") ? n.push(s) : n.push("'" + s + "'")
        }
        n = n.join(","), r = "normal", "o" === t.O ? r = "oblique" : "i" === t.O && (r = "italic"), i.style.cssText = "display:block;position:absolute;top:-9999px;left:-9999px;font-size:300px;width:auto;height:auto;line-height:normal;margin:0;padding:0;font-variant:normal;white-space:nowrap;font-family:" + n + ";" + ("font-style:" + r + ";font-weight:" + (t.Z + "00") + ";")
    }

    function q(e) {
        a(e.d, "body", e.o)
    }

    function F(e, t, n, i, r, o, s, a) {
        this.$ = e, this.ka = t, this.d = n, this.m = i, this.k = r, this.I = a || "BESbswy", this.v = {}, this.X = o || 3e3, this.ca = s || null, this.H = this.u = this.t = null, this.t = new L(this.d, this.I), this.u = new L(this.d, this.I), this.H = new L(this.d, this.I), O(this.t, new k("serif", E(this.m))), O(this.u, new k("sans-serif", E(this.m))), O(this.H, new k("monospace", E(this.m))), q(this.t), q(this.u), q(this.H), this.v.serif = this.t.o.offsetWidth, this.v["sans-serif"] = this.u.o.offsetWidth, this.v.monospace = this.H.o.offsetWidth
    }

    function $(e, t, n) {
        for (var i in ee)
            if (ee.hasOwnProperty(i) && t === e.v[ee[i]] && n === e.v[ee[i]]) return !0;
        return !1
    }

    function W(e) {
        var t = e.t.o.offsetWidth,
            n = e.u.o.offsetWidth;
        t === e.v.serif && n === e.v["sans-serif"] || e.k.ga && $(e, t, n) ? K() - e.oa >= e.X ? e.k.ga && $(e, t, n) && (null === e.ca || e.ca.hasOwnProperty(e.m.getName())) ? P(e, e.$) : P(e, e.ka) : H(e) : P(e, e.$)
    }

    function H(e) {
        setTimeout(o(function() {
            W(this)
        }, e), 50)
    }

    function P(e, t) {
        e.t.remove(), e.u.remove(), e.H.remove(), t(e.m)
    }

    function M(e, t, n, i) {
        this.d = t, this.A = n, this.S = 0, this.ea = this.ba = !1, this.X = i, this.k = e.k
    }

    function I(e, t, n, i, r) {
        if (n = n || {}, 0 === t.length && r) A(e.A);
        else
            for (e.S += t.length, r && (e.ba = r), r = 0; r < t.length; r++) {
                var s = t[r],
                    a = n[s.getName()],
                    u = e.A,
                    c = s;
                u.F && l(u.q, [u.h.e(u.j, c.getName(), E(c).toString(), "loading")]), j(u, "fontloading", c), u = null, u = new F(o(e.ia, e), o(e.ja, e), e.d, s, e.k, e.X, i, a), u.start()
            }
    }

    function B(e) {
        0 == --e.S && e.ba && (e.ea ? (e = e.A, e.F && l(e.q, [e.h.e(e.j, "active")], [e.h.e(e.j, "loading"), e.h.e(e.j, "inactive")]), j(e, "active")) : A(e.A))
    }

    function R(e) {
        this.K = e, this.B = new D, this.pa = new y(e.navigator.userAgent), this.a = this.pa.parse(), this.U = this.V = 0, this.R = this.T = !0
    }

    function _(e, t, n, i, r) {
        var o = 0 == --e.V;
        (e.R || e.T) && setTimeout(function() {
            I(t, n, i || null, r || null, o)
        }, 0)
    }

    function z(e, t, n) {
        this.P = e ? e : t + te, this.s = [], this.W = [], this.fa = n || ""
    }

    function X(e) {
        this.s = e, this.da = [], this.M = {}
    }

    function U(e, t) {
        this.a = new y(navigator.userAgent).parse(), this.d = e, this.f = t
    }

    function V(e, t) {
        this.d = e, this.f = t, this.p = []
    }

    function G(e, t) {
        this.d = e, this.f = t, this.p = []
    }

    function J(e, t) {
        this.d = e, this.f = t, this.p = []
    }

    function Y(e, t) {
        this.d = e, this.f = t
    }
    var K = Date.now || function() {
        return +new Date
    };
    s.prototype.createElement = function(e, t, n) {
        if (e = this.G.createElement(e), t)
            for (var i in t) t.hasOwnProperty(i) && ("style" == i ? e.style.cssText = t[i] : e.setAttribute(i, t[i]));
        return n && e.appendChild(this.G.createTextNode(n)), e
    };
    var Q = /^([0-9]+)(?:[\._-]([0-9]+))?(?:[\._-]([0-9]+))?(?:[\._+-]?(.*))?$/;
    g.prototype.compare = function(e) {
        return this.c > e.c || this.c === e.c && this.g > e.g || this.c === e.c && this.g === e.g && this.D > e.D ? 1 : this.c < e.c || this.c === e.c && this.g < e.g || this.c === e.c && this.g === e.g && this.D < e.D ? -1 : 0
    }, g.prototype.toString = function() {
        return [this.c, this.g || "", this.D || "", this.e || ""].join("")
    }, v.prototype.getName = function() {
        return this.N
    };
    var Z = new v("Unknown", 0, 0, 0, 0, 0, 0, new h(!1, !1));
    y.prototype.parse = function() {
        var e;
        if (-1 != this.a.indexOf("MSIE") || -1 != this.a.indexOf("Trident/")) {
            e = x(this);
            var t = m(w(this)),
                n = null,
                i = T(this.a, /Trident\/([\d\w\.]+)/, 1),
                n = m(-1 != this.a.indexOf("MSIE") ? T(this.a, /MSIE ([\d\w\.]+)/, 1) : T(this.a, /rv:([\d\w\.]+)/, 1));
            "" != i && m(i), e = new v("MSIE", 0, 0, 0, 0, 0, 0, new h("Windows" == e && 6 <= n.c || "Windows Phone" == e && 8 <= t.c, !1))
        } else if (-1 != this.a.indexOf("Opera")) e: if (e = m(T(this.a, /Presto\/([\d\w\.]+)/, 1)), m(w(this)), null !== e.c || m(T(this.a, /rv:([^\)]+)/, 1)), -1 != this.a.indexOf("Opera Mini/")) e = m(T(this.a, /Opera Mini\/([\d\.]+)/, 1)), e = new v("OperaMini", 0, 0, 0, x(this), 0, 0, new h(!1, !1));
            else {
                if (-1 != this.a.indexOf("Version/") && (e = m(T(this.a, /Version\/([\d\.]+)/, 1)), null !== e.c)) {
                    e = new v("Opera", 0, 0, 0, x(this), 0, 0, new h(10 <= e.c, !1));
                    break e
                }
                e = m(T(this.a, /Opera[\/ ]([\d\.]+)/, 1)), e = null !== e.c ? new v("Opera", 0, 0, 0, x(this), 0, 0, new h(10 <= e.c, !1)) : new v("Opera", 0, 0, 0, x(this), 0, 0, new h(!1, !1))
            } else /OPR\/[\d.]+/.test(this.a) ? e = b(this) : /AppleWeb(K|k)it/.test(this.a) ? e = b(this) : -1 != this.a.indexOf("Gecko") ? (e = "Unknown", t = new g, m(w(this)), t = !1, -1 != this.a.indexOf("Firefox") ? (e = "Firefox", t = m(T(this.a, /Firefox\/([\d\w\.]+)/, 1)), t = 3 <= t.c && 5 <= t.g) : -1 != this.a.indexOf("Mozilla") && (e = "Mozilla"), n = m(T(this.a, /rv:([^\)]+)/, 1)), t || (t = 1 < n.c || 1 == n.c && 9 < n.g || 1 == n.c && 9 == n.g && 2 <= n.D), e = new v(e, 0, 0, 0, x(this), 0, 0, new h(t, !1))) : e = Z;
        return e
    }, C.prototype.e = function(e) {
        for (var t = [], n = 0; n < arguments.length; n++) t.push(arguments[n].replace(/[\W_]+/g, "").toLowerCase());
        return t.join(this.ma)
    }, k.prototype.getName = function() {
        return this.N
    }, L.prototype.remove = function() {
        var e = this.o;
        e.parentNode && e.parentNode.removeChild(e)
    };
    var ee = {
        sa: "serif",
        ra: "sans-serif",
        qa: "monospace"
    };
    F.prototype.start = function() {
        this.oa = K(), O(this.t, new k(this.m.getName() + ",serif", E(this.m))), O(this.u, new k(this.m.getName() + ",sans-serif", E(this.m))), W(this)
    }, M.prototype.ia = function(e) {
        var t = this.A;
        t.F && l(t.q, [t.h.e(t.j, e.getName(), E(e).toString(), "active")], [t.h.e(t.j, e.getName(), E(e).toString(), "loading"), t.h.e(t.j, e.getName(), E(e).toString(), "inactive")]), j(t, "fontactive", e), this.ea = !0, B(this)
    }, M.prototype.ja = function(e) {
        var t = this.A;
        if (t.F) {
            var n = c(t.q, t.h.e(t.j, e.getName(), E(e).toString(), "active")),
                i = [],
                r = [t.h.e(t.j, e.getName(), E(e).toString(), "loading")];
            n || i.push(t.h.e(t.j, e.getName(), E(e).toString(), "inactive")), l(t.q, i, r)
        }
        j(t, "fontinactive", e), B(this)
    }, R.prototype.load = function(e) {
        this.d = new s(this.K, e.context || this.K), this.T = !1 !== e.events, this.R = !1 !== e.classes;
        var t = new N(this.d, e),
            n = [],
            i = e.timeout;
        t.F && l(t.q, [t.h.e(t.j, "loading")]), j(t, "loading");
        var r, n = this.B,
            a = this.d,
            u = [];
        for (r in e)
            if (e.hasOwnProperty(r)) {
                var c = n.C[r];
                c && u.push(c(e[r], a))
            }
        for (n = u, this.U = this.V = n.length, e = new M(this.a, this.d, t, i), i = 0, r = n.length; r > i; i++) a = n[i], a.L(this.a, o(this.la, this, a, t, e))
    }, R.prototype.la = function(e, t, n, i) {
        var r = this;
        i ? e.load(function(e, t, i) {
            _(r, n, e, t, i)
        }) : (e = 0 == --this.V, this.U--, e && 0 == this.U ? A(t) : (this.R || this.T) && I(n, [], {}, null, e))
    };
    var te = "//fonts.googleapis.com/css";
    z.prototype.e = function() {
        if (0 == this.s.length) throw Error("No fonts to load!");
        if (-1 != this.P.indexOf("kit=")) return this.P;
        for (var e = this.s.length, t = [], n = 0; e > n; n++) t.push(this.s[n].replace(/ /g, "+"));
        return e = this.P + "?family=" + t.join("%7C"), 0 < this.W.length && (e += "&subset=" + this.W.join(",")), 0 < this.fa.length && (e += "&text=" + encodeURIComponent(this.fa)), e
    };
    var ne = {
            latin: "BESbswy",
            cyrillic: "&#1081;&#1103;&#1046;",
            greek: "&#945;&#946;&#931;",
            khmer: "&#x1780;&#x1781;&#x1782;",
            Hanuman: "&#x1780;&#x1781;&#x1782;"
        },
        ie = {
            thin: "1",
            extralight: "2",
            "extra-light": "2",
            ultralight: "2",
            "ultra-light": "2",
            light: "3",
            regular: "4",
            book: "4",
            medium: "5",
            "semi-bold": "6",
            semibold: "6",
            "demi-bold": "6",
            demibold: "6",
            bold: "7",
            "extra-bold": "8",
            extrabold: "8",
            "ultra-bold": "8",
            ultrabold: "8",
            black: "9",
            heavy: "9",
            l: "3",
            r: "4",
            b: "7"
        },
        re = {
            i: "i",
            italic: "i",
            n: "n",
            normal: "n"
        },
        oe = /^(thin|(?:(?:extra|ultra)-?)?light|regular|book|medium|(?:(?:semi|demi|extra|ultra)-?)?bold|black|heavy|l|r|b|[1-9]00)?(n|i|normal|italic)?$/;
    X.prototype.parse = function() {
        for (var e = this.s.length, t = 0; e > t; t++) {
            var n = this.s[t].split(":"),
                i = n[0].replace(/\+/g, " "),
                r = ["n4"];
            if (2 <= n.length) {
                var o, s = n[1];
                if (o = [], s)
                    for (var s = s.split(","), a = s.length, u = 0; a > u; u++) {
                        var l;
                        if (l = s[u], l.match(/^[\w-]+$/)) {
                            l = oe.exec(l.toLowerCase());
                            var c = void 0;
                            if (null == l) c = "";
                            else {
                                if (c = void 0, c = l[1], null == c || "" == c) c = "4";
                                else var f = ie[c],
                                    c = f ? f : isNaN(c) ? "4" : c.substr(0, 1);
                                l = l[2], c = [null == l || "" == l ? "n" : re[l], c].join("")
                            }
                            l = c
                        } else l = "";
                        l && o.push(l)
                    }
                0 < o.length && (r = o), 3 == n.length && (n = n[2], o = [], n = n ? n.split(",") : o, 0 < n.length && (n = ne[n[0]]) && (this.M[i] = n))
            }
            for (this.M[i] || (n = ne[i]) && (this.M[i] = n), n = 0; n < r.length; n += 1) this.da.push(new k(i, r[n]))
        }
    };
    var se = {
        Arimo: !0,
        Cousine: !0,
        Tinos: !0
    };
    U.prototype.L = function(e, t) {
        t(e.k.Y)
    }, U.prototype.load = function(e) {
        var t = this.d;
        "MSIE" == this.a.getName() && 1 != this.f.blocking ? u(t, o(this.aa, this, e)) : this.aa(e)
    }, U.prototype.aa = function(e) {
        for (var t = this.d, n = new z(this.f.api, f(t), this.f.text), i = this.f.families, r = i.length, o = 0; r > o; o++) {
            var s = i[o].split(":");
            3 == s.length && n.W.push(s.pop());
            var a = "";
            2 == s.length && "" != s[1] && (a = ":"), n.s.push(s.join(a))
        }
        i = new X(i), i.parse(), d(t, n.e()), e(i.da, i.M, se)
    }, V.prototype.J = function(e) {
        var t = this.d;
        return f(this.d) + (this.f.api || "//f.fontdeck.com/s/css/js/") + (t.w.location.hostname || t.K.location.hostname) + "/" + e + ".js"
    }, V.prototype.L = function(e, t) {
        var n = this.f.id,
            i = this.d.w,
            r = this;
        n ? (i.__webfontfontdeckmodule__ || (i.__webfontfontdeckmodule__ = {}), i.__webfontfontdeckmodule__[n] = function(e, n) {
            for (var i = 0, o = n.fonts.length; o > i; ++i) {
                var s = n.fonts[i];
                r.p.push(new k(s.name, S("font-weight:" + s.weight + ";font-style:" + s.style)))
            }
            t(e)
        }, p(this.d, this.J(n), function(e) {
            e && t(!1)
        })) : t(!1)
    }, V.prototype.load = function(e) {
        e(this.p)
    }, G.prototype.J = function(e) {
        var t = f(this.d);
        return (this.f.api || t + "//use.typekit.net") + "/" + e + ".js"
    }, G.prototype.L = function(e, t) {
        var n = this.f.id,
            i = this.d.w,
            r = this;
        n ? p(this.d, this.J(n), function(e) {
            if (e) t(!1);
            else {
                if (i.Typekit && i.Typekit.config && i.Typekit.config.fn) {
                    e = i.Typekit.config.fn;
                    for (var n = 0; n < e.length; n += 2)
                        for (var o = e[n], s = e[n + 1], a = 0; a < s.length; a++) r.p.push(new k(o, s[a]));
                    try {
                        i.Typekit.load({
                            events: !1,
                            classes: !1
                        })
                    } catch (u) {}
                }
                t(!0)
            }
        }, 2e3) : t(!1)
    }, G.prototype.load = function(e) {
        e(this.p)
    }, J.prototype.L = function(e, t) {
        var n = this,
            i = n.f.projectId,
            r = n.f.version;
        if (i) {
            var o = n.d.w;
            p(this.d, n.J(i, r), function(r) {
                if (r) t(!1);
                else {
                    if (o["__mti_fntLst" + i] && (r = o["__mti_fntLst" + i]()))
                        for (var s = 0; s < r.length; s++) n.p.push(new k(r[s].fontfamily));
                    t(e.k.Y)
                }
            }).id = "__MonotypeAPIScript__" + i
        } else t(!1)
    }, J.prototype.J = function(e, t) {
        var n = f(this.d),
            i = (this.f.api || "fast.fonts.net/jsapi").replace(/^.*http(s?):(\/\/)?/, "");
        return n + "//" + i + "/" + e + ".js" + (t ? "?v=" + t : "")
    }, J.prototype.load = function(e) {
        e(this.p)
    }, Y.prototype.load = function(e) {
        var t, n, i = this.f.urls || [],
            r = this.f.families || [],
            o = this.f.testStrings || {};
        for (t = 0, n = i.length; n > t; t++) d(this.d, i[t]);
        for (i = [], t = 0, n = r.length; n > t; t++) {
            var s = r[t].split(":");
            if (s[1])
                for (var a = s[1].split(","), u = 0; u < a.length; u += 1) i.push(new k(s[0], a[u]));
            else i.push(new k(s[0]))
        }
        e(i, o)
    }, Y.prototype.L = function(e, t) {
        return t(e.k.Y)
    };
    var ae = new R(this);
    ae.B.C.custom = function(e, t) {
        return new Y(t, e)
    }, ae.B.C.fontdeck = function(e, t) {
        return new V(t, e)
    }, ae.B.C.monotype = function(e, t) {
        return new J(t, e)
    }, ae.B.C.typekit = function(e, t) {
        return new G(t, e)
    }, ae.B.C.google = function(e, t) {
        return new U(t, e)
    }, this.WebFont || (this.WebFont = {}, this.WebFont.load = o(ae.load, ae), this.WebFontConfig && ae.load(this.WebFontConfig))
}(this, document),
function(e, t) {
    "object" == typeof module && "object" == typeof module.exports ? module.exports = e.document ? t(e, !0) : function(e) {
        if (!e.document) throw new Error("jQuery requires a window with a document");
        return t(e)
    } : t(e)
}("undefined" != typeof window ? window : this, function(e, t) {
    function n(e) {
        var t = !!e && "length" in e && e.length,
            n = oe.type(e);
        return "function" === n || oe.isWindow(e) ? !1 : "array" === n || 0 === t || "number" == typeof t && t > 0 && t - 1 in e
    }

    function i(e, t, n) {
        if (oe.isFunction(t)) return oe.grep(e, function(e, i) {
            return !!t.call(e, i, e) !== n
        });
        if (t.nodeType) return oe.grep(e, function(e) {
            return e === t !== n
        });
        if ("string" == typeof t) {
            if (ge.test(t)) return oe.filter(t, e, n);
            t = oe.filter(t, e)
        }
        return oe.grep(e, function(e) {
            return Z.call(t, e) > -1 !== n
        })
    }

    function r(e, t) {
        for (;
            (e = e[t]) && 1 !== e.nodeType;);
        return e
    }

    function o(e) {
        var t = {};
        return oe.each(e.match(be) || [], function(e, n) {
            t[n] = !0
        }), t
    }

    function s() {
        J.removeEventListener("DOMContentLoaded", s), e.removeEventListener("load", s), oe.ready()
    }

    function a() {
        this.expando = oe.expando + a.uid++
    }

    function u(e, t, n) {
        var i;
        if (void 0 === n && 1 === e.nodeType)
            if (i = "data-" + t.replace(Ae, "-$&").toLowerCase(), n = e.getAttribute(i), "string" == typeof n) {
                try {
                    n = "true" === n ? !0 : "false" === n ? !1 : "null" === n ? null : +n + "" === n ? +n : Ne.test(n) ? oe.parseJSON(n) : n
                } catch (r) {}
                Se.set(e, t, n)
            } else n = void 0;
        return n
    }

    function l(e, t, n, i) {
        var r, o = 1,
            s = 20,
            a = i ? function() {
                return i.cur()
            } : function() {
                return oe.css(e, t, "")
            },
            u = a(),
            l = n && n[3] || (oe.cssNumber[t] ? "" : "px"),
            c = (oe.cssNumber[t] || "px" !== l && +u) && De.exec(oe.css(e, t));
        if (c && c[3] !== l) {
            l = l || c[3], n = n || [], c = +u || 1;
            do o = o || ".5", c /= o, oe.style(e, t, c + l); while (o !== (o = a() / u) && 1 !== o && --s)
        }
        return n && (c = +c || +u || 0, r = n[1] ? c + (n[1] + 1) * n[2] : +n[2], i && (i.unit = l, i.start = c, i.end = r)), r
    }

    function c(e, t) {
        var n = "undefined" != typeof e.getElementsByTagName ? e.getElementsByTagName(t || "*") : "undefined" != typeof e.querySelectorAll ? e.querySelectorAll(t || "*") : [];
        return void 0 === t || t && oe.nodeName(e, t) ? oe.merge([e], n) : n
    }

    function f(e, t) {
        for (var n = 0, i = e.length; i > n; n++) Ee.set(e[n], "globalEval", !t || Ee.get(t[n], "globalEval"))
    }

    function d(e, t, n, i, r) {
        for (var o, s, a, u, l, d, p = t.createDocumentFragment(), h = [], g = 0, m = e.length; m > g; g++)
            if (o = e[g], o || 0 === o)
                if ("object" === oe.type(o)) oe.merge(h, o.nodeType ? [o] : o);
                else if (He.test(o)) {
            for (s = s || p.appendChild(t.createElement("div")), a = (Fe.exec(o) || ["", ""])[1].toLowerCase(), u = We[a] || We._default, s.innerHTML = u[1] + oe.htmlPrefilter(o) + u[2], d = u[0]; d--;) s = s.lastChild;
            oe.merge(h, s.childNodes), s = p.firstChild, s.textContent = ""
        } else h.push(t.createTextNode(o));
        for (p.textContent = "", g = 0; o = h[g++];)
            if (i && oe.inArray(o, i) > -1) r && r.push(o);
            else if (l = oe.contains(o.ownerDocument, o), s = c(p.appendChild(o), "script"), l && f(s), n)
            for (d = 0; o = s[d++];) $e.test(o.type || "") && n.push(o);
        return p
    }

    function p() {
        return !0
    }

    function h() {
        return !1
    }

    function g() {
        try {
            return J.activeElement
        } catch (e) {}
    }

    function m(e, t, n, i, r, o) {
        var s, a;
        if ("object" == typeof t) {
            "string" != typeof n && (i = i || n, n = void 0);
            for (a in t) m(e, a, n, i, t[a], o);
            return e
        }
        if (null == i && null == r ? (r = n, i = n = void 0) : null == r && ("string" == typeof n ? (r = i, i = void 0) : (r = i, i = n, n = void 0)), r === !1) r = h;
        else if (!r) return e;
        return 1 === o && (s = r, r = function(e) {
            return oe().off(e), s.apply(this, arguments)
        }, r.guid = s.guid || (s.guid = oe.guid++)), e.each(function() {
            oe.event.add(this, t, r, i, n)
        })
    }

    function v(e, t) {
        return oe.nodeName(e, "table") && oe.nodeName(11 !== t.nodeType ? t : t.firstChild, "tr") ? e.getElementsByTagName("tbody")[0] || e.appendChild(e.ownerDocument.createElement("tbody")) : e
    }

    function y(e) {
        return e.type = (null !== e.getAttribute("type")) + "/" + e.type, e
    }

    function x(e) {
        var t = ze.exec(e.type);
        return t ? e.type = t[1] : e.removeAttribute("type"), e
    }

    function w(e, t) {
        var n, i, r, o, s, a, u, l;
        if (1 === t.nodeType) {
            if (Ee.hasData(e) && (o = Ee.access(e), s = Ee.set(t, o), l = o.events)) {
                delete s.handle, s.events = {};
                for (r in l)
                    for (n = 0, i = l[r].length; i > n; n++) oe.event.add(t, r, l[r][n])
            }
            Se.hasData(e) && (a = Se.access(e), u = oe.extend({}, a), Se.set(t, u))
        }
    }

    function b(e, t) {
        var n = t.nodeName.toLowerCase();
        "input" === n && qe.test(e.type) ? t.checked = e.checked : "input" !== n && "textarea" !== n || (t.defaultValue = e.defaultValue)
    }

    function T(e, t, n, i) {
        t = K.apply([], t);
        var r, o, s, a, u, l, f = 0,
            p = e.length,
            h = p - 1,
            g = t[0],
            m = oe.isFunction(g);
        if (m || p > 1 && "string" == typeof g && !ie.checkClone && _e.test(g)) return e.each(function(r) {
            var o = e.eq(r);
            m && (t[0] = g.call(this, r, o.html())), T(o, t, n, i)
        });
        if (p && (r = d(t, e[0].ownerDocument, !1, e, i), o = r.firstChild, 1 === r.childNodes.length && (r = o), o || i)) {
            for (s = oe.map(c(r, "script"), y), a = s.length; p > f; f++) u = r, f !== h && (u = oe.clone(u, !0, !0), a && oe.merge(s, c(u, "script"))), n.call(e[f], u, f);
            if (a)
                for (l = s[s.length - 1].ownerDocument, oe.map(s, x), f = 0; a > f; f++) u = s[f], $e.test(u.type || "") && !Ee.access(u, "globalEval") && oe.contains(l, u) && (u.src ? oe._evalUrl && oe._evalUrl(u.src) : oe.globalEval(u.textContent.replace(Xe, "")))
        }
        return e
    }

    function C(e, t, n) {
        for (var i, r = t ? oe.filter(t, e) : e, o = 0; null != (i = r[o]); o++) n || 1 !== i.nodeType || oe.cleanData(c(i)), i.parentNode && (n && oe.contains(i.ownerDocument, i) && f(c(i, "script")), i.parentNode.removeChild(i));
        return e
    }

    function k(e, t) {
        var n = oe(t.createElement(e)).appendTo(t.body),
            i = oe.css(n[0], "display");
        return n.detach(), i
    }

    function E(e) {
        var t = J,
            n = Ve[e];
        return n || (n = k(e, t), "none" !== n && n || (Ue = (Ue || oe("<iframe frameborder='0' width='0' height='0'/>")).appendTo(t.documentElement), t = Ue[0].contentDocument, t.write(), t.close(), n = k(e, t), Ue.detach()), Ve[e] = n), n
    }

    function S(e, t, n) {
        var i, r, o, s, a = e.style;
        return n = n || Ye(e), s = n ? n.getPropertyValue(t) || n[t] : void 0, "" !== s && void 0 !== s || oe.contains(e.ownerDocument, e) || (s = oe.style(e, t)), n && !ie.pixelMarginRight() && Je.test(s) && Ge.test(t) && (i = a.width, r = a.minWidth, o = a.maxWidth, a.minWidth = a.maxWidth = a.width = s, s = n.width, a.width = i, a.minWidth = r, a.maxWidth = o), void 0 !== s ? s + "" : s
    }

    function N(e, t) {
        return {
            get: function() {
                return e() ? void delete this.get : (this.get = t).apply(this, arguments)
            }
        }
    }

    function A(e) {
        if (e in it) return e;
        for (var t = e[0].toUpperCase() + e.slice(1), n = nt.length; n--;)
            if (e = nt[n] + t, e in it) return e
    }

    function j(e, t, n) {
        var i = De.exec(t);
        return i ? Math.max(0, i[2] - (n || 0)) + (i[3] || "px") : t
    }

    function D(e, t, n, i, r) {
        for (var o = n === (i ? "border" : "content") ? 4 : "width" === t ? 1 : 0, s = 0; 4 > o; o += 2) "margin" === n && (s += oe.css(e, n + Le[o], !0, r)), i ? ("content" === n && (s -= oe.css(e, "padding" + Le[o], !0, r)), "margin" !== n && (s -= oe.css(e, "border" + Le[o] + "Width", !0, r))) : (s += oe.css(e, "padding" + Le[o], !0, r), "padding" !== n && (s += oe.css(e, "border" + Le[o] + "Width", !0, r)));
        return s
    }

    function L(e, t, n) {
        var i = !0,
            r = "width" === t ? e.offsetWidth : e.offsetHeight,
            o = Ye(e),
            s = "border-box" === oe.css(e, "boxSizing", !1, o);
        if (0 >= r || null == r) {
            if (r = S(e, t, o), (0 > r || null == r) && (r = e.style[t]), Je.test(r)) return r;
            i = s && (ie.boxSizingReliable() || r === e.style[t]), r = parseFloat(r) || 0
        }
        return r + D(e, t, n || (s ? "border" : "content"), i, o) + "px"
    }

    function O(e, t) {
        for (var n, i, r, o = [], s = 0, a = e.length; a > s; s++) i = e[s], i.style && (o[s] = Ee.get(i, "olddisplay"), n = i.style.display, t ? (o[s] || "none" !== n || (i.style.display = ""), "" === i.style.display && Oe(i) && (o[s] = Ee.access(i, "olddisplay", E(i.nodeName)))) : (r = Oe(i), "none" === n && r || Ee.set(i, "olddisplay", r ? n : oe.css(i, "display"))));
        for (s = 0; a > s; s++) i = e[s], i.style && (t && "none" !== i.style.display && "" !== i.style.display || (i.style.display = t ? o[s] || "" : "none"));
        return e
    }

    function q(e, t, n, i, r) {
        return new q.prototype.init(e, t, n, i, r)
    }

    function F() {
        return e.setTimeout(function() {
            rt = void 0
        }), rt = oe.now()
    }

    function $(e, t) {
        var n, i = 0,
            r = {
                height: e
            };
        for (t = t ? 1 : 0; 4 > i; i += 2 - t) n = Le[i], r["margin" + n] = r["padding" + n] = e;
        return t && (r.opacity = r.width = e), r
    }

    function W(e, t, n) {
        for (var i, r = (M.tweeners[t] || []).concat(M.tweeners["*"]), o = 0, s = r.length; s > o; o++)
            if (i = r[o].call(n, t, e)) return i
    }

    function H(e, t, n) {
        var i, r, o, s, a, u, l, c, f = this,
            d = {},
            p = e.style,
            h = e.nodeType && Oe(e),
            g = Ee.get(e, "fxshow");
        n.queue || (a = oe._queueHooks(e, "fx"), null == a.unqueued && (a.unqueued = 0, u = a.empty.fire, a.empty.fire = function() {
            a.unqueued || u()
        }), a.unqueued++, f.always(function() {
            f.always(function() {
                a.unqueued--, oe.queue(e, "fx").length || a.empty.fire()
            })
        })), 1 === e.nodeType && ("height" in t || "width" in t) && (n.overflow = [p.overflow, p.overflowX, p.overflowY], l = oe.css(e, "display"), c = "none" === l ? Ee.get(e, "olddisplay") || E(e.nodeName) : l, "inline" === c && "none" === oe.css(e, "float") && (p.display = "inline-block")), n.overflow && (p.overflow = "hidden", f.always(function() {
            p.overflow = n.overflow[0], p.overflowX = n.overflow[1], p.overflowY = n.overflow[2]
        }));
        for (i in t)
            if (r = t[i], st.exec(r)) {
                if (delete t[i], o = o || "toggle" === r, r === (h ? "hide" : "show")) {
                    if ("show" !== r || !g || void 0 === g[i]) continue;
                    h = !0
                }
                d[i] = g && g[i] || oe.style(e, i)
            } else l = void 0;
        if (oe.isEmptyObject(d)) "inline" === ("none" === l ? E(e.nodeName) : l) && (p.display = l);
        else {
            g ? "hidden" in g && (h = g.hidden) : g = Ee.access(e, "fxshow", {}), o && (g.hidden = !h), h ? oe(e).show() : f.done(function() {
                oe(e).hide()
            }), f.done(function() {
                var t;
                Ee.remove(e, "fxshow");
                for (t in d) oe.style(e, t, d[t])
            });
            for (i in d) s = W(h ? g[i] : 0, i, f), i in g || (g[i] = s.start, h && (s.end = s.start, s.start = "width" === i || "height" === i ? 1 : 0))
        }
    }

    function P(e, t) {
        var n, i, r, o, s;
        for (n in e)
            if (i = oe.camelCase(n), r = t[i], o = e[n], oe.isArray(o) && (r = o[1], o = e[n] = o[0]), n !== i && (e[i] = o, delete e[n]), s = oe.cssHooks[i], s && "expand" in s) {
                o = s.expand(o), delete e[i];
                for (n in o) n in e || (e[n] = o[n], t[n] = r)
            } else t[i] = r
    }

    function M(e, t, n) {
        var i, r, o = 0,
            s = M.prefilters.length,
            a = oe.Deferred().always(function() {
                delete u.elem
            }),
            u = function() {
                if (r) return !1;
                for (var t = rt || F(), n = Math.max(0, l.startTime + l.duration - t), i = n / l.duration || 0, o = 1 - i, s = 0, u = l.tweens.length; u > s; s++) l.tweens[s].run(o);
                return a.notifyWith(e, [l, o, n]), 1 > o && u ? n : (a.resolveWith(e, [l]), !1)
            },
            l = a.promise({
                elem: e,
                props: oe.extend({}, t),
                opts: oe.extend(!0, {
                    specialEasing: {},
                    easing: oe.easing._default
                }, n),
                originalProperties: t,
                originalOptions: n,
                startTime: rt || F(),
                duration: n.duration,
                tweens: [],
                createTween: function(t, n) {
                    var i = oe.Tween(e, l.opts, t, n, l.opts.specialEasing[t] || l.opts.easing);
                    return l.tweens.push(i), i
                },
                stop: function(t) {
                    var n = 0,
                        i = t ? l.tweens.length : 0;
                    if (r) return this;
                    for (r = !0; i > n; n++) l.tweens[n].run(1);
                    return t ? (a.notifyWith(e, [l, 1, 0]), a.resolveWith(e, [l, t])) : a.rejectWith(e, [l, t]), this
                }
            }),
            c = l.props;
        for (P(c, l.opts.specialEasing); s > o; o++)
            if (i = M.prefilters[o].call(l, e, c, l.opts)) return oe.isFunction(i.stop) && (oe._queueHooks(l.elem, l.opts.queue).stop = oe.proxy(i.stop, i)), i;
        return oe.map(c, W, l), oe.isFunction(l.opts.start) && l.opts.start.call(e, l), oe.fx.timer(oe.extend(u, {
            elem: e,
            anim: l,
            queue: l.opts.queue
        })), l.progress(l.opts.progress).done(l.opts.done, l.opts.complete).fail(l.opts.fail).always(l.opts.always)
    }

    function I(e) {
        return e.getAttribute && e.getAttribute("class") || ""
    }

    function B(e) {
        return function(t, n) {
            "string" != typeof t && (n = t, t = "*");
            var i, r = 0,
                o = t.toLowerCase().match(be) || [];
            if (oe.isFunction(n))
                for (; i = o[r++];) "+" === i[0] ? (i = i.slice(1) || "*", (e[i] = e[i] || []).unshift(n)) : (e[i] = e[i] || []).push(n)
        }
    }

    function R(e, t, n, i) {
        function r(a) {
            var u;
            return o[a] = !0, oe.each(e[a] || [], function(e, a) {
                var l = a(t, n, i);
                return "string" != typeof l || s || o[l] ? s ? !(u = l) : void 0 : (t.dataTypes.unshift(l), r(l), !1)
            }), u
        }
        var o = {},
            s = e === St;
        return r(t.dataTypes[0]) || !o["*"] && r("*")
    }

    function _(e, t) {
        var n, i, r = oe.ajaxSettings.flatOptions || {};
        for (n in t) void 0 !== t[n] && ((r[n] ? e : i || (i = {}))[n] = t[n]);
        return i && oe.extend(!0, e, i), e
    }

    function z(e, t, n) {
        for (var i, r, o, s, a = e.contents, u = e.dataTypes;
            "*" === u[0];) u.shift(), void 0 === i && (i = e.mimeType || t.getResponseHeader("Content-Type"));
        if (i)
            for (r in a)
                if (a[r] && a[r].test(i)) {
                    u.unshift(r);
                    break
                }
        if (u[0] in n) o = u[0];
        else {
            for (r in n) {
                if (!u[0] || e.converters[r + " " + u[0]]) {
                    o = r;
                    break
                }
                s || (s = r)
            }
            o = o || s
        }
        return o ? (o !== u[0] && u.unshift(o), n[o]) : void 0
    }

    function X(e, t, n, i) {
        var r, o, s, a, u, l = {},
            c = e.dataTypes.slice();
        if (c[1])
            for (s in e.converters) l[s.toLowerCase()] = e.converters[s];
        for (o = c.shift(); o;)
            if (e.responseFields[o] && (n[e.responseFields[o]] = t), !u && i && e.dataFilter && (t = e.dataFilter(t, e.dataType)), u = o, o = c.shift())
                if ("*" === o) o = u;
                else if ("*" !== u && u !== o) {
            if (s = l[u + " " + o] || l["* " + o], !s)
                for (r in l)
                    if (a = r.split(" "), a[1] === o && (s = l[u + " " + a[0]] || l["* " + a[0]])) {
                        s === !0 ? s = l[r] : l[r] !== !0 && (o = a[0], c.unshift(a[1]));
                        break
                    }
            if (s !== !0)
                if (s && e["throws"]) t = s(t);
                else try {
                    t = s(t)
                } catch (f) {
                    return {
                        state: "parsererror",
                        error: s ? f : "No conversion from " + u + " to " + o
                    }
                }
        }
        return {
            state: "success",
            data: t
        }
    }

    function U(e, t, n, i) {
        var r;
        if (oe.isArray(t)) oe.each(t, function(t, r) {
            n || Dt.test(e) ? i(e, r) : U(e + "[" + ("object" == typeof r && null != r ? t : "") + "]", r, n, i)
        });
        else if (n || "object" !== oe.type(t)) i(e, t);
        else
            for (r in t) U(e + "[" + r + "]", t[r], n, i)
    }

    function V(e) {
        return oe.isWindow(e) ? e : 9 === e.nodeType && e.defaultView
    }
    var G = [],
        J = e.document,
        Y = G.slice,
        K = G.concat,
        Q = G.push,
        Z = G.indexOf,
        ee = {},
        te = ee.toString,
        ne = ee.hasOwnProperty,
        ie = {},
        re = "2.2.4",
        oe = function(e, t) {
            return new oe.fn.init(e, t)
        },
        se = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g,
        ae = /^-ms-/,
        ue = /-([\da-z])/gi,
        le = function(e, t) {
            return t.toUpperCase()
        };
    oe.fn = oe.prototype = {
        jquery: re,
        constructor: oe,
        selector: "",
        length: 0,
        toArray: function() {
            return Y.call(this)
        },
        get: function(e) {
            return null != e ? 0 > e ? this[e + this.length] : this[e] : Y.call(this)
        },
        pushStack: function(e) {
            var t = oe.merge(this.constructor(), e);
            return t.prevObject = this, t.context = this.context, t
        },
        each: function(e) {
            return oe.each(this, e)
        },
        map: function(e) {
            return this.pushStack(oe.map(this, function(t, n) {
                return e.call(t, n, t)
            }))
        },
        slice: function() {
            return this.pushStack(Y.apply(this, arguments))
        },
        first: function() {
            return this.eq(0)
        },
        last: function() {
            return this.eq(-1)
        },
        eq: function(e) {
            var t = this.length,
                n = +e + (0 > e ? t : 0);
            return this.pushStack(n >= 0 && t > n ? [this[n]] : [])
        },
        end: function() {
            return this.prevObject || this.constructor()
        },
        push: Q,
        sort: G.sort,
        splice: G.splice
    }, oe.extend = oe.fn.extend = function() {
        var e, t, n, i, r, o, s = arguments[0] || {},
            a = 1,
            u = arguments.length,
            l = !1;
        for ("boolean" == typeof s && (l = s, s = arguments[a] || {}, a++), "object" == typeof s || oe.isFunction(s) || (s = {}), a === u && (s = this, a--); u > a; a++)
            if (null != (e = arguments[a]))
                for (t in e) n = s[t], i = e[t], s !== i && (l && i && (oe.isPlainObject(i) || (r = oe.isArray(i))) ? (r ? (r = !1, o = n && oe.isArray(n) ? n : []) : o = n && oe.isPlainObject(n) ? n : {}, s[t] = oe.extend(l, o, i)) : void 0 !== i && (s[t] = i));
        return s
    }, oe.extend({
        expando: "jQuery" + (re + Math.random()).replace(/\D/g, ""),
        isReady: !0,
        error: function(e) {
            throw new Error(e)
        },
        noop: function() {},
        isFunction: function(e) {
            return "function" === oe.type(e)
        },
        isArray: Array.isArray,
        isWindow: function(e) {
            return null != e && e === e.window
        },
        isNumeric: function(e) {
            var t = e && e.toString();
            return !oe.isArray(e) && t - parseFloat(t) + 1 >= 0
        },
        isPlainObject: function(e) {
            var t;
            if ("object" !== oe.type(e) || e.nodeType || oe.isWindow(e)) return !1;
            if (e.constructor && !ne.call(e, "constructor") && !ne.call(e.constructor.prototype || {}, "isPrototypeOf")) return !1;
            for (t in e);
            return void 0 === t || ne.call(e, t)
        },
        isEmptyObject: function(e) {
            var t;
            for (t in e) return !1;
            return !0
        },
        type: function(e) {
            return null == e ? e + "" : "object" == typeof e || "function" == typeof e ? ee[te.call(e)] || "object" : typeof e
        },
        globalEval: function(e) {
            var t, n = eval;
            e = oe.trim(e), e && (1 === e.indexOf("use strict") ? (t = J.createElement("script"), t.text = e, J.head.appendChild(t).parentNode.removeChild(t)) : n(e))
        },
        camelCase: function(e) {
            return e.replace(ae, "ms-").replace(ue, le)
        },
        nodeName: function(e, t) {
            return e.nodeName && e.nodeName.toLowerCase() === t.toLowerCase()
        },
        each: function(e, t) {
            var i, r = 0;
            if (n(e))
                for (i = e.length; i > r && t.call(e[r], r, e[r]) !== !1; r++);
            else
                for (r in e)
                    if (t.call(e[r], r, e[r]) === !1) break; return e
        },
        trim: function(e) {
            return null == e ? "" : (e + "").replace(se, "")
        },
        makeArray: function(e, t) {
            var i = t || [];
            return null != e && (n(Object(e)) ? oe.merge(i, "string" == typeof e ? [e] : e) : Q.call(i, e)), i
        },
        inArray: function(e, t, n) {
            return null == t ? -1 : Z.call(t, e, n)
        },
        merge: function(e, t) {
            for (var n = +t.length, i = 0, r = e.length; n > i; i++) e[r++] = t[i];
            return e.length = r, e
        },
        grep: function(e, t, n) {
            for (var i, r = [], o = 0, s = e.length, a = !n; s > o; o++) i = !t(e[o], o), i !== a && r.push(e[o]);
            return r
        },
        map: function(e, t, i) {
            var r, o, s = 0,
                a = [];
            if (n(e))
                for (r = e.length; r > s; s++) o = t(e[s], s, i), null != o && a.push(o);
            else
                for (s in e) o = t(e[s], s, i), null != o && a.push(o);
            return K.apply([], a)
        },
        guid: 1,
        proxy: function(e, t) {
            var n, i, r;
            return "string" == typeof t && (n = e[t], t = e, e = n), oe.isFunction(e) ? (i = Y.call(arguments, 2), r = function() {
                    return e.apply(t || this, i.concat(Y.call(arguments)))
                }, r.guid = e.guid = e.guid || oe.guid++,
                r) : void 0
        },
        now: Date.now,
        support: ie
    }), "function" == typeof Symbol && (oe.fn[Symbol.iterator] = G[Symbol.iterator]), oe.each("Boolean Number String Function Array Date RegExp Object Error Symbol".split(" "), function(e, t) {
        ee["[object " + t + "]"] = t.toLowerCase()
    });
    var ce = function(e) {
        function t(e, t, n, i) {
            var r, o, s, a, u, l, f, p, h = t && t.ownerDocument,
                g = t ? t.nodeType : 9;
            if (n = n || [], "string" != typeof e || !e || 1 !== g && 9 !== g && 11 !== g) return n;
            if (!i && ((t ? t.ownerDocument || t : I) !== O && L(t), t = t || O, F)) {
                if (11 !== g && (l = ve.exec(e)))
                    if (r = l[1]) {
                        if (9 === g) {
                            if (!(s = t.getElementById(r))) return n;
                            if (s.id === r) return n.push(s), n
                        } else if (h && (s = h.getElementById(r)) && P(t, s) && s.id === r) return n.push(s), n
                    } else {
                        if (l[2]) return Q.apply(n, t.getElementsByTagName(e)), n;
                        if ((r = l[3]) && b.getElementsByClassName && t.getElementsByClassName) return Q.apply(n, t.getElementsByClassName(r)), n
                    }
                if (b.qsa && !X[e + " "] && (!$ || !$.test(e))) {
                    if (1 !== g) h = t, p = e;
                    else if ("object" !== t.nodeName.toLowerCase()) {
                        for ((a = t.getAttribute("id")) ? a = a.replace(xe, "\\$&") : t.setAttribute("id", a = M), f = E(e), o = f.length, u = de.test(a) ? "#" + a : "[id='" + a + "']"; o--;) f[o] = u + " " + d(f[o]);
                        p = f.join(","), h = ye.test(e) && c(t.parentNode) || t
                    }
                    if (p) try {
                        return Q.apply(n, h.querySelectorAll(p)), n
                    } catch (m) {} finally {
                        a === M && t.removeAttribute("id")
                    }
                }
            }
            return N(e.replace(ae, "$1"), t, n, i)
        }

        function n() {
            function e(n, i) {
                return t.push(n + " ") > T.cacheLength && delete e[t.shift()], e[n + " "] = i
            }
            var t = [];
            return e
        }

        function i(e) {
            return e[M] = !0, e
        }

        function r(e) {
            var t = O.createElement("div");
            try {
                return !!e(t)
            } catch (n) {
                return !1
            } finally {
                t.parentNode && t.parentNode.removeChild(t), t = null
            }
        }

        function o(e, t) {
            for (var n = e.split("|"), i = n.length; i--;) T.attrHandle[n[i]] = t
        }

        function s(e, t) {
            var n = t && e,
                i = n && 1 === e.nodeType && 1 === t.nodeType && (~t.sourceIndex || V) - (~e.sourceIndex || V);
            if (i) return i;
            if (n)
                for (; n = n.nextSibling;)
                    if (n === t) return -1;
            return e ? 1 : -1
        }

        function a(e) {
            return function(t) {
                var n = t.nodeName.toLowerCase();
                return "input" === n && t.type === e
            }
        }

        function u(e) {
            return function(t) {
                var n = t.nodeName.toLowerCase();
                return ("input" === n || "button" === n) && t.type === e
            }
        }

        function l(e) {
            return i(function(t) {
                return t = +t, i(function(n, i) {
                    for (var r, o = e([], n.length, t), s = o.length; s--;) n[r = o[s]] && (n[r] = !(i[r] = n[r]))
                })
            })
        }

        function c(e) {
            return e && "undefined" != typeof e.getElementsByTagName && e
        }

        function f() {}

        function d(e) {
            for (var t = 0, n = e.length, i = ""; n > t; t++) i += e[t].value;
            return i
        }

        function p(e, t, n) {
            var i = t.dir,
                r = n && "parentNode" === i,
                o = R++;
            return t.first ? function(t, n, o) {
                for (; t = t[i];)
                    if (1 === t.nodeType || r) return e(t, n, o)
            } : function(t, n, s) {
                var a, u, l, c = [B, o];
                if (s) {
                    for (; t = t[i];)
                        if ((1 === t.nodeType || r) && e(t, n, s)) return !0
                } else
                    for (; t = t[i];)
                        if (1 === t.nodeType || r) {
                            if (l = t[M] || (t[M] = {}), u = l[t.uniqueID] || (l[t.uniqueID] = {}), (a = u[i]) && a[0] === B && a[1] === o) return c[2] = a[2];
                            if (u[i] = c, c[2] = e(t, n, s)) return !0
                        }
            }
        }

        function h(e) {
            return e.length > 1 ? function(t, n, i) {
                for (var r = e.length; r--;)
                    if (!e[r](t, n, i)) return !1;
                return !0
            } : e[0]
        }

        function g(e, n, i) {
            for (var r = 0, o = n.length; o > r; r++) t(e, n[r], i);
            return i
        }

        function m(e, t, n, i, r) {
            for (var o, s = [], a = 0, u = e.length, l = null != t; u > a; a++)(o = e[a]) && (n && !n(o, i, r) || (s.push(o), l && t.push(a)));
            return s
        }

        function v(e, t, n, r, o, s) {
            return r && !r[M] && (r = v(r)), o && !o[M] && (o = v(o, s)), i(function(i, s, a, u) {
                var l, c, f, d = [],
                    p = [],
                    h = s.length,
                    v = i || g(t || "*", a.nodeType ? [a] : a, []),
                    y = !e || !i && t ? v : m(v, d, e, a, u),
                    x = n ? o || (i ? e : h || r) ? [] : s : y;
                if (n && n(y, x, a, u), r)
                    for (l = m(x, p), r(l, [], a, u), c = l.length; c--;)(f = l[c]) && (x[p[c]] = !(y[p[c]] = f));
                if (i) {
                    if (o || e) {
                        if (o) {
                            for (l = [], c = x.length; c--;)(f = x[c]) && l.push(y[c] = f);
                            o(null, x = [], l, u)
                        }
                        for (c = x.length; c--;)(f = x[c]) && (l = o ? ee(i, f) : d[c]) > -1 && (i[l] = !(s[l] = f))
                    }
                } else x = m(x === s ? x.splice(h, x.length) : x), o ? o(null, s, x, u) : Q.apply(s, x)
            })
        }

        function y(e) {
            for (var t, n, i, r = e.length, o = T.relative[e[0].type], s = o || T.relative[" "], a = o ? 1 : 0, u = p(function(e) {
                    return e === t
                }, s, !0), l = p(function(e) {
                    return ee(t, e) > -1
                }, s, !0), c = [function(e, n, i) {
                    var r = !o && (i || n !== A) || ((t = n).nodeType ? u(e, n, i) : l(e, n, i));
                    return t = null, r
                }]; r > a; a++)
                if (n = T.relative[e[a].type]) c = [p(h(c), n)];
                else {
                    if (n = T.filter[e[a].type].apply(null, e[a].matches), n[M]) {
                        for (i = ++a; r > i && !T.relative[e[i].type]; i++);
                        return v(a > 1 && h(c), a > 1 && d(e.slice(0, a - 1).concat({
                            value: " " === e[a - 2].type ? "*" : ""
                        })).replace(ae, "$1"), n, i > a && y(e.slice(a, i)), r > i && y(e = e.slice(i)), r > i && d(e))
                    }
                    c.push(n)
                }
            return h(c)
        }

        function x(e, n) {
            var r = n.length > 0,
                o = e.length > 0,
                s = function(i, s, a, u, l) {
                    var c, f, d, p = 0,
                        h = "0",
                        g = i && [],
                        v = [],
                        y = A,
                        x = i || o && T.find.TAG("*", l),
                        w = B += null == y ? 1 : Math.random() || .1,
                        b = x.length;
                    for (l && (A = s === O || s || l); h !== b && null != (c = x[h]); h++) {
                        if (o && c) {
                            for (f = 0, s || c.ownerDocument === O || (L(c), a = !F); d = e[f++];)
                                if (d(c, s || O, a)) {
                                    u.push(c);
                                    break
                                }
                            l && (B = w)
                        }
                        r && ((c = !d && c) && p--, i && g.push(c))
                    }
                    if (p += h, r && h !== p) {
                        for (f = 0; d = n[f++];) d(g, v, s, a);
                        if (i) {
                            if (p > 0)
                                for (; h--;) g[h] || v[h] || (v[h] = Y.call(u));
                            v = m(v)
                        }
                        Q.apply(u, v), l && !i && v.length > 0 && p + n.length > 1 && t.uniqueSort(u)
                    }
                    return l && (B = w, A = y), g
                };
            return r ? i(s) : s
        }
        var w, b, T, C, k, E, S, N, A, j, D, L, O, q, F, $, W, H, P, M = "sizzle" + 1 * new Date,
            I = e.document,
            B = 0,
            R = 0,
            _ = n(),
            z = n(),
            X = n(),
            U = function(e, t) {
                return e === t && (D = !0), 0
            },
            V = 1 << 31,
            G = {}.hasOwnProperty,
            J = [],
            Y = J.pop,
            K = J.push,
            Q = J.push,
            Z = J.slice,
            ee = function(e, t) {
                for (var n = 0, i = e.length; i > n; n++)
                    if (e[n] === t) return n;
                return -1
            },
            te = "checked|selected|async|autofocus|autoplay|controls|defer|disabled|hidden|ismap|loop|multiple|open|readonly|required|scoped",
            ne = "[\\x20\\t\\r\\n\\f]",
            ie = "(?:\\\\.|[\\w-]|[^\\x00-\\xa0])+",
            re = "\\[" + ne + "*(" + ie + ")(?:" + ne + "*([*^$|!~]?=)" + ne + "*(?:'((?:\\\\.|[^\\\\'])*)'|\"((?:\\\\.|[^\\\\\"])*)\"|(" + ie + "))|)" + ne + "*\\]",
            oe = ":(" + ie + ")(?:\\((('((?:\\\\.|[^\\\\'])*)'|\"((?:\\\\.|[^\\\\\"])*)\")|((?:\\\\.|[^\\\\()[\\]]|" + re + ")*)|.*)\\)|)",
            se = new RegExp(ne + "+", "g"),
            ae = new RegExp("^" + ne + "+|((?:^|[^\\\\])(?:\\\\.)*)" + ne + "+$", "g"),
            ue = new RegExp("^" + ne + "*," + ne + "*"),
            le = new RegExp("^" + ne + "*([>+~]|" + ne + ")" + ne + "*"),
            ce = new RegExp("=" + ne + "*([^\\]'\"]*?)" + ne + "*\\]", "g"),
            fe = new RegExp(oe),
            de = new RegExp("^" + ie + "$"),
            pe = {
                ID: new RegExp("^#(" + ie + ")"),
                CLASS: new RegExp("^\\.(" + ie + ")"),
                TAG: new RegExp("^(" + ie + "|[*])"),
                ATTR: new RegExp("^" + re),
                PSEUDO: new RegExp("^" + oe),
                CHILD: new RegExp("^:(only|first|last|nth|nth-last)-(child|of-type)(?:\\(" + ne + "*(even|odd|(([+-]|)(\\d*)n|)" + ne + "*(?:([+-]|)" + ne + "*(\\d+)|))" + ne + "*\\)|)", "i"),
                bool: new RegExp("^(?:" + te + ")$", "i"),
                needsContext: new RegExp("^" + ne + "*[>+~]|:(even|odd|eq|gt|lt|nth|first|last)(?:\\(" + ne + "*((?:-\\d)?\\d*)" + ne + "*\\)|)(?=[^-]|$)", "i")
            },
            he = /^(?:input|select|textarea|button)$/i,
            ge = /^h\d$/i,
            me = /^[^{]+\{\s*\[native \w/,
            ve = /^(?:#([\w-]+)|(\w+)|\.([\w-]+))$/,
            ye = /[+~]/,
            xe = /'|\\/g,
            we = new RegExp("\\\\([\\da-f]{1,6}" + ne + "?|(" + ne + ")|.)", "ig"),
            be = function(e, t, n) {
                var i = "0x" + t - 65536;
                return i !== i || n ? t : 0 > i ? String.fromCharCode(i + 65536) : String.fromCharCode(i >> 10 | 55296, 1023 & i | 56320)
            },
            Te = function() {
                L()
            };
        try {
            Q.apply(J = Z.call(I.childNodes), I.childNodes), J[I.childNodes.length].nodeType
        } catch (Ce) {
            Q = {
                apply: J.length ? function(e, t) {
                    K.apply(e, Z.call(t))
                } : function(e, t) {
                    for (var n = e.length, i = 0; e[n++] = t[i++];);
                    e.length = n - 1
                }
            }
        }
        b = t.support = {}, k = t.isXML = function(e) {
            var t = e && (e.ownerDocument || e).documentElement;
            return t ? "HTML" !== t.nodeName : !1
        }, L = t.setDocument = function(e) {
            var t, n, i = e ? e.ownerDocument || e : I;
            return i !== O && 9 === i.nodeType && i.documentElement ? (O = i, q = O.documentElement, F = !k(O), (n = O.defaultView) && n.top !== n && (n.addEventListener ? n.addEventListener("unload", Te, !1) : n.attachEvent && n.attachEvent("onunload", Te)), b.attributes = r(function(e) {
                return e.className = "i", !e.getAttribute("className")
            }), b.getElementsByTagName = r(function(e) {
                return e.appendChild(O.createComment("")), !e.getElementsByTagName("*").length
            }), b.getElementsByClassName = me.test(O.getElementsByClassName), b.getById = r(function(e) {
                return q.appendChild(e).id = M, !O.getElementsByName || !O.getElementsByName(M).length
            }), b.getById ? (T.find.ID = function(e, t) {
                if ("undefined" != typeof t.getElementById && F) {
                    var n = t.getElementById(e);
                    return n ? [n] : []
                }
            }, T.filter.ID = function(e) {
                var t = e.replace(we, be);
                return function(e) {
                    return e.getAttribute("id") === t
                }
            }) : (delete T.find.ID, T.filter.ID = function(e) {
                var t = e.replace(we, be);
                return function(e) {
                    var n = "undefined" != typeof e.getAttributeNode && e.getAttributeNode("id");
                    return n && n.value === t
                }
            }), T.find.TAG = b.getElementsByTagName ? function(e, t) {
                return "undefined" != typeof t.getElementsByTagName ? t.getElementsByTagName(e) : b.qsa ? t.querySelectorAll(e) : void 0
            } : function(e, t) {
                var n, i = [],
                    r = 0,
                    o = t.getElementsByTagName(e);
                if ("*" === e) {
                    for (; n = o[r++];) 1 === n.nodeType && i.push(n);
                    return i
                }
                return o
            }, T.find.CLASS = b.getElementsByClassName && function(e, t) {
                return "undefined" != typeof t.getElementsByClassName && F ? t.getElementsByClassName(e) : void 0
            }, W = [], $ = [], (b.qsa = me.test(O.querySelectorAll)) && (r(function(e) {
                q.appendChild(e).innerHTML = "<a id='" + M + "'></a><select id='" + M + "-\r\\' msallowcapture=''><option selected=''></option></select>", e.querySelectorAll("[msallowcapture^='']").length && $.push("[*^$]=" + ne + "*(?:''|\"\")"), e.querySelectorAll("[selected]").length || $.push("\\[" + ne + "*(?:value|" + te + ")"), e.querySelectorAll("[id~=" + M + "-]").length || $.push("~="), e.querySelectorAll(":checked").length || $.push(":checked"), e.querySelectorAll("a#" + M + "+*").length || $.push(".#.+[+~]")
            }), r(function(e) {
                var t = O.createElement("input");
                t.setAttribute("type", "hidden"), e.appendChild(t).setAttribute("name", "D"), e.querySelectorAll("[name=d]").length && $.push("name" + ne + "*[*^$|!~]?="), e.querySelectorAll(":enabled").length || $.push(":enabled", ":disabled"), e.querySelectorAll("*,:x"), $.push(",.*:")
            })), (b.matchesSelector = me.test(H = q.matches || q.webkitMatchesSelector || q.mozMatchesSelector || q.oMatchesSelector || q.msMatchesSelector)) && r(function(e) {
                b.disconnectedMatch = H.call(e, "div"), H.call(e, "[s!='']:x"), W.push("!=", oe)
            }), $ = $.length && new RegExp($.join("|")), W = W.length && new RegExp(W.join("|")), t = me.test(q.compareDocumentPosition), P = t || me.test(q.contains) ? function(e, t) {
                var n = 9 === e.nodeType ? e.documentElement : e,
                    i = t && t.parentNode;
                return e === i || !(!i || 1 !== i.nodeType || !(n.contains ? n.contains(i) : e.compareDocumentPosition && 16 & e.compareDocumentPosition(i)))
            } : function(e, t) {
                if (t)
                    for (; t = t.parentNode;)
                        if (t === e) return !0;
                return !1
            }, U = t ? function(e, t) {
                if (e === t) return D = !0, 0;
                var n = !e.compareDocumentPosition - !t.compareDocumentPosition;
                return n ? n : (n = (e.ownerDocument || e) === (t.ownerDocument || t) ? e.compareDocumentPosition(t) : 1, 1 & n || !b.sortDetached && t.compareDocumentPosition(e) === n ? e === O || e.ownerDocument === I && P(I, e) ? -1 : t === O || t.ownerDocument === I && P(I, t) ? 1 : j ? ee(j, e) - ee(j, t) : 0 : 4 & n ? -1 : 1)
            } : function(e, t) {
                if (e === t) return D = !0, 0;
                var n, i = 0,
                    r = e.parentNode,
                    o = t.parentNode,
                    a = [e],
                    u = [t];
                if (!r || !o) return e === O ? -1 : t === O ? 1 : r ? -1 : o ? 1 : j ? ee(j, e) - ee(j, t) : 0;
                if (r === o) return s(e, t);
                for (n = e; n = n.parentNode;) a.unshift(n);
                for (n = t; n = n.parentNode;) u.unshift(n);
                for (; a[i] === u[i];) i++;
                return i ? s(a[i], u[i]) : a[i] === I ? -1 : u[i] === I ? 1 : 0
            }, O) : O
        }, t.matches = function(e, n) {
            return t(e, null, null, n)
        }, t.matchesSelector = function(e, n) {
            if ((e.ownerDocument || e) !== O && L(e), n = n.replace(ce, "='$1']"), b.matchesSelector && F && !X[n + " "] && (!W || !W.test(n)) && (!$ || !$.test(n))) try {
                var i = H.call(e, n);
                if (i || b.disconnectedMatch || e.document && 11 !== e.document.nodeType) return i
            } catch (r) {}
            return t(n, O, null, [e]).length > 0
        }, t.contains = function(e, t) {
            return (e.ownerDocument || e) !== O && L(e), P(e, t)
        }, t.attr = function(e, t) {
            (e.ownerDocument || e) !== O && L(e);
            var n = T.attrHandle[t.toLowerCase()],
                i = n && G.call(T.attrHandle, t.toLowerCase()) ? n(e, t, !F) : void 0;
            return void 0 !== i ? i : b.attributes || !F ? e.getAttribute(t) : (i = e.getAttributeNode(t)) && i.specified ? i.value : null
        }, t.error = function(e) {
            throw new Error("Syntax error, unrecognized expression: " + e)
        }, t.uniqueSort = function(e) {
            var t, n = [],
                i = 0,
                r = 0;
            if (D = !b.detectDuplicates, j = !b.sortStable && e.slice(0), e.sort(U), D) {
                for (; t = e[r++];) t === e[r] && (i = n.push(r));
                for (; i--;) e.splice(n[i], 1)
            }
            return j = null, e
        }, C = t.getText = function(e) {
            var t, n = "",
                i = 0,
                r = e.nodeType;
            if (r) {
                if (1 === r || 9 === r || 11 === r) {
                    if ("string" == typeof e.textContent) return e.textContent;
                    for (e = e.firstChild; e; e = e.nextSibling) n += C(e)
                } else if (3 === r || 4 === r) return e.nodeValue
            } else
                for (; t = e[i++];) n += C(t);
            return n
        }, T = t.selectors = {
            cacheLength: 50,
            createPseudo: i,
            match: pe,
            attrHandle: {},
            find: {},
            relative: {
                ">": {
                    dir: "parentNode",
                    first: !0
                },
                " ": {
                    dir: "parentNode"
                },
                "+": {
                    dir: "previousSibling",
                    first: !0
                },
                "~": {
                    dir: "previousSibling"
                }
            },
            preFilter: {
                ATTR: function(e) {
                    return e[1] = e[1].replace(we, be), e[3] = (e[3] || e[4] || e[5] || "").replace(we, be), "~=" === e[2] && (e[3] = " " + e[3] + " "), e.slice(0, 4)
                },
                CHILD: function(e) {
                    return e[1] = e[1].toLowerCase(), "nth" === e[1].slice(0, 3) ? (e[3] || t.error(e[0]), e[4] = +(e[4] ? e[5] + (e[6] || 1) : 2 * ("even" === e[3] || "odd" === e[3])), e[5] = +(e[7] + e[8] || "odd" === e[3])) : e[3] && t.error(e[0]), e
                },
                PSEUDO: function(e) {
                    var t, n = !e[6] && e[2];
                    return pe.CHILD.test(e[0]) ? null : (e[3] ? e[2] = e[4] || e[5] || "" : n && fe.test(n) && (t = E(n, !0)) && (t = n.indexOf(")", n.length - t) - n.length) && (e[0] = e[0].slice(0, t), e[2] = n.slice(0, t)), e.slice(0, 3))
                }
            },
            filter: {
                TAG: function(e) {
                    var t = e.replace(we, be).toLowerCase();
                    return "*" === e ? function() {
                        return !0
                    } : function(e) {
                        return e.nodeName && e.nodeName.toLowerCase() === t
                    }
                },
                CLASS: function(e) {
                    var t = _[e + " "];
                    return t || (t = new RegExp("(^|" + ne + ")" + e + "(" + ne + "|$)")) && _(e, function(e) {
                        return t.test("string" == typeof e.className && e.className || "undefined" != typeof e.getAttribute && e.getAttribute("class") || "")
                    })
                },
                ATTR: function(e, n, i) {
                    return function(r) {
                        var o = t.attr(r, e);
                        return null == o ? "!=" === n : n ? (o += "", "=" === n ? o === i : "!=" === n ? o !== i : "^=" === n ? i && 0 === o.indexOf(i) : "*=" === n ? i && o.indexOf(i) > -1 : "$=" === n ? i && o.slice(-i.length) === i : "~=" === n ? (" " + o.replace(se, " ") + " ").indexOf(i) > -1 : "|=" === n ? o === i || o.slice(0, i.length + 1) === i + "-" : !1) : !0
                    }
                },
                CHILD: function(e, t, n, i, r) {
                    var o = "nth" !== e.slice(0, 3),
                        s = "last" !== e.slice(-4),
                        a = "of-type" === t;
                    return 1 === i && 0 === r ? function(e) {
                        return !!e.parentNode
                    } : function(t, n, u) {
                        var l, c, f, d, p, h, g = o !== s ? "nextSibling" : "previousSibling",
                            m = t.parentNode,
                            v = a && t.nodeName.toLowerCase(),
                            y = !u && !a,
                            x = !1;
                        if (m) {
                            if (o) {
                                for (; g;) {
                                    for (d = t; d = d[g];)
                                        if (a ? d.nodeName.toLowerCase() === v : 1 === d.nodeType) return !1;
                                    h = g = "only" === e && !h && "nextSibling"
                                }
                                return !0
                            }
                            if (h = [s ? m.firstChild : m.lastChild], s && y) {
                                for (d = m, f = d[M] || (d[M] = {}), c = f[d.uniqueID] || (f[d.uniqueID] = {}), l = c[e] || [], p = l[0] === B && l[1], x = p && l[2], d = p && m.childNodes[p]; d = ++p && d && d[g] || (x = p = 0) || h.pop();)
                                    if (1 === d.nodeType && ++x && d === t) {
                                        c[e] = [B, p, x];
                                        break
                                    }
                            } else if (y && (d = t, f = d[M] || (d[M] = {}), c = f[d.uniqueID] || (f[d.uniqueID] = {}), l = c[e] || [], p = l[0] === B && l[1], x = p), x === !1)
                                for (;
                                    (d = ++p && d && d[g] || (x = p = 0) || h.pop()) && ((a ? d.nodeName.toLowerCase() !== v : 1 !== d.nodeType) || !++x || (y && (f = d[M] || (d[M] = {}), c = f[d.uniqueID] || (f[d.uniqueID] = {}), c[e] = [B, x]), d !== t)););
                            return x -= r, x === i || x % i === 0 && x / i >= 0
                        }
                    }
                },
                PSEUDO: function(e, n) {
                    var r, o = T.pseudos[e] || T.setFilters[e.toLowerCase()] || t.error("unsupported pseudo: " + e);
                    return o[M] ? o(n) : o.length > 1 ? (r = [e, e, "", n], T.setFilters.hasOwnProperty(e.toLowerCase()) ? i(function(e, t) {
                        for (var i, r = o(e, n), s = r.length; s--;) i = ee(e, r[s]), e[i] = !(t[i] = r[s])
                    }) : function(e) {
                        return o(e, 0, r)
                    }) : o
                }
            },
            pseudos: {
                not: i(function(e) {
                    var t = [],
                        n = [],
                        r = S(e.replace(ae, "$1"));
                    return r[M] ? i(function(e, t, n, i) {
                        for (var o, s = r(e, null, i, []), a = e.length; a--;)(o = s[a]) && (e[a] = !(t[a] = o))
                    }) : function(e, i, o) {
                        return t[0] = e, r(t, null, o, n), t[0] = null, !n.pop()
                    }
                }),
                has: i(function(e) {
                    return function(n) {
                        return t(e, n).length > 0
                    }
                }),
                contains: i(function(e) {
                    return e = e.replace(we, be),
                        function(t) {
                            return (t.textContent || t.innerText || C(t)).indexOf(e) > -1
                        }
                }),
                lang: i(function(e) {
                    return de.test(e || "") || t.error("unsupported lang: " + e), e = e.replace(we, be).toLowerCase(),
                        function(t) {
                            var n;
                            do
                                if (n = F ? t.lang : t.getAttribute("xml:lang") || t.getAttribute("lang")) return n = n.toLowerCase(), n === e || 0 === n.indexOf(e + "-");
                            while ((t = t.parentNode) && 1 === t.nodeType);
                            return !1
                        }
                }),
                target: function(t) {
                    var n = e.location && e.location.hash;
                    return n && n.slice(1) === t.id
                },
                root: function(e) {
                    return e === q
                },
                focus: function(e) {
                    return e === O.activeElement && (!O.hasFocus || O.hasFocus()) && !!(e.type || e.href || ~e.tabIndex)
                },
                enabled: function(e) {
                    return e.disabled === !1
                },
                disabled: function(e) {
                    return e.disabled === !0
                },
                checked: function(e) {
                    var t = e.nodeName.toLowerCase();
                    return "input" === t && !!e.checked || "option" === t && !!e.selected
                },
                selected: function(e) {
                    return e.parentNode && e.parentNode.selectedIndex, e.selected === !0
                },
                empty: function(e) {
                    for (e = e.firstChild; e; e = e.nextSibling)
                        if (e.nodeType < 6) return !1;
                    return !0
                },
                parent: function(e) {
                    return !T.pseudos.empty(e)
                },
                header: function(e) {
                    return ge.test(e.nodeName)
                },
                input: function(e) {
                    return he.test(e.nodeName)
                },
                button: function(e) {
                    var t = e.nodeName.toLowerCase();
                    return "input" === t && "button" === e.type || "button" === t
                },
                text: function(e) {
                    var t;
                    return "input" === e.nodeName.toLowerCase() && "text" === e.type && (null == (t = e.getAttribute("type")) || "text" === t.toLowerCase())
                },
                first: l(function() {
                    return [0]
                }),
                last: l(function(e, t) {
                    return [t - 1]
                }),
                eq: l(function(e, t, n) {
                    return [0 > n ? n + t : n]
                }),
                even: l(function(e, t) {
                    for (var n = 0; t > n; n += 2) e.push(n);
                    return e
                }),
                odd: l(function(e, t) {
                    for (var n = 1; t > n; n += 2) e.push(n);
                    return e
                }),
                lt: l(function(e, t, n) {
                    for (var i = 0 > n ? n + t : n; --i >= 0;) e.push(i);
                    return e
                }),
                gt: l(function(e, t, n) {
                    for (var i = 0 > n ? n + t : n; ++i < t;) e.push(i);
                    return e
                })
            }
        }, T.pseudos.nth = T.pseudos.eq;
        for (w in {
                radio: !0,
                checkbox: !0,
                file: !0,
                password: !0,
                image: !0
            }) T.pseudos[w] = a(w);
        for (w in {
                submit: !0,
                reset: !0
            }) T.pseudos[w] = u(w);
        return f.prototype = T.filters = T.pseudos, T.setFilters = new f, E = t.tokenize = function(e, n) {
            var i, r, o, s, a, u, l, c = z[e + " "];
            if (c) return n ? 0 : c.slice(0);
            for (a = e, u = [], l = T.preFilter; a;) {
                i && !(r = ue.exec(a)) || (r && (a = a.slice(r[0].length) || a), u.push(o = [])), i = !1, (r = le.exec(a)) && (i = r.shift(), o.push({
                    value: i,
                    type: r[0].replace(ae, " ")
                }), a = a.slice(i.length));
                for (s in T.filter) !(r = pe[s].exec(a)) || l[s] && !(r = l[s](r)) || (i = r.shift(), o.push({
                    value: i,
                    type: s,
                    matches: r
                }), a = a.slice(i.length));
                if (!i) break
            }
            return n ? a.length : a ? t.error(e) : z(e, u).slice(0)
        }, S = t.compile = function(e, t) {
            var n, i = [],
                r = [],
                o = X[e + " "];
            if (!o) {
                for (t || (t = E(e)), n = t.length; n--;) o = y(t[n]), o[M] ? i.push(o) : r.push(o);
                o = X(e, x(r, i)), o.selector = e
            }
            return o
        }, N = t.select = function(e, t, n, i) {
            var r, o, s, a, u, l = "function" == typeof e && e,
                f = !i && E(e = l.selector || e);
            if (n = n || [], 1 === f.length) {
                if (o = f[0] = f[0].slice(0), o.length > 2 && "ID" === (s = o[0]).type && b.getById && 9 === t.nodeType && F && T.relative[o[1].type]) {
                    if (t = (T.find.ID(s.matches[0].replace(we, be), t) || [])[0], !t) return n;
                    l && (t = t.parentNode), e = e.slice(o.shift().value.length)
                }
                for (r = pe.needsContext.test(e) ? 0 : o.length; r-- && (s = o[r], !T.relative[a = s.type]);)
                    if ((u = T.find[a]) && (i = u(s.matches[0].replace(we, be), ye.test(o[0].type) && c(t.parentNode) || t))) {
                        if (o.splice(r, 1), e = i.length && d(o), !e) return Q.apply(n, i), n;
                        break
                    }
            }
            return (l || S(e, f))(i, t, !F, n, !t || ye.test(e) && c(t.parentNode) || t), n
        }, b.sortStable = M.split("").sort(U).join("") === M, b.detectDuplicates = !!D, L(), b.sortDetached = r(function(e) {
            return 1 & e.compareDocumentPosition(O.createElement("div"))
        }), r(function(e) {
            return e.innerHTML = "<a href='#'></a>", "#" === e.firstChild.getAttribute("href")
        }) || o("type|href|height|width", function(e, t, n) {
            return n ? void 0 : e.getAttribute(t, "type" === t.toLowerCase() ? 1 : 2)
        }), b.attributes && r(function(e) {
            return e.innerHTML = "<input/>", e.firstChild.setAttribute("value", ""), "" === e.firstChild.getAttribute("value")
        }) || o("value", function(e, t, n) {
            return n || "input" !== e.nodeName.toLowerCase() ? void 0 : e.defaultValue
        }), r(function(e) {
            return null == e.getAttribute("disabled")
        }) || o(te, function(e, t, n) {
            var i;
            return n ? void 0 : e[t] === !0 ? t.toLowerCase() : (i = e.getAttributeNode(t)) && i.specified ? i.value : null
        }), t
    }(e);
    oe.find = ce, oe.expr = ce.selectors, oe.expr[":"] = oe.expr.pseudos, oe.uniqueSort = oe.unique = ce.uniqueSort, oe.text = ce.getText, oe.isXMLDoc = ce.isXML, oe.contains = ce.contains;
    var fe = function(e, t, n) {
            for (var i = [], r = void 0 !== n;
                (e = e[t]) && 9 !== e.nodeType;)
                if (1 === e.nodeType) {
                    if (r && oe(e).is(n)) break;
                    i.push(e)
                }
            return i
        },
        de = function(e, t) {
            for (var n = []; e; e = e.nextSibling) 1 === e.nodeType && e !== t && n.push(e);
            return n
        },
        pe = oe.expr.match.needsContext,
        he = /^<([\w-]+)\s*\/?>(?:<\/\1>|)$/,
        ge = /^.[^:#\[\.,]*$/;
    oe.filter = function(e, t, n) {
        var i = t[0];
        return n && (e = ":not(" + e + ")"), 1 === t.length && 1 === i.nodeType ? oe.find.matchesSelector(i, e) ? [i] : [] : oe.find.matches(e, oe.grep(t, function(e) {
            return 1 === e.nodeType
        }))
    }, oe.fn.extend({
        find: function(e) {
            var t, n = this.length,
                i = [],
                r = this;
            if ("string" != typeof e) return this.pushStack(oe(e).filter(function() {
                for (t = 0; n > t; t++)
                    if (oe.contains(r[t], this)) return !0
            }));
            for (t = 0; n > t; t++) oe.find(e, r[t], i);
            return i = this.pushStack(n > 1 ? oe.unique(i) : i), i.selector = this.selector ? this.selector + " " + e : e, i
        },
        filter: function(e) {
            return this.pushStack(i(this, e || [], !1))
        },
        not: function(e) {
            return this.pushStack(i(this, e || [], !0))
        },
        is: function(e) {
            return !!i(this, "string" == typeof e && pe.test(e) ? oe(e) : e || [], !1).length
        }
    });
    var me, ve = /^(?:\s*(<[\w\W]+>)[^>]*|#([\w-]*))$/,
        ye = oe.fn.init = function(e, t, n) {
            var i, r;
            if (!e) return this;
            if (n = n || me, "string" == typeof e) {
                if (i = "<" === e[0] && ">" === e[e.length - 1] && e.length >= 3 ? [null, e, null] : ve.exec(e), !i || !i[1] && t) return !t || t.jquery ? (t || n).find(e) : this.constructor(t).find(e);
                if (i[1]) {
                    if (t = t instanceof oe ? t[0] : t, oe.merge(this, oe.parseHTML(i[1], t && t.nodeType ? t.ownerDocument || t : J, !0)), he.test(i[1]) && oe.isPlainObject(t))
                        for (i in t) oe.isFunction(this[i]) ? this[i](t[i]) : this.attr(i, t[i]);
                    return this
                }
                return r = J.getElementById(i[2]), r && r.parentNode && (this.length = 1, this[0] = r), this.context = J, this.selector = e, this
            }
            return e.nodeType ? (this.context = this[0] = e, this.length = 1, this) : oe.isFunction(e) ? void 0 !== n.ready ? n.ready(e) : e(oe) : (void 0 !== e.selector && (this.selector = e.selector, this.context = e.context), oe.makeArray(e, this))
        };
    ye.prototype = oe.fn, me = oe(J);
    var xe = /^(?:parents|prev(?:Until|All))/,
        we = {
            children: !0,
            contents: !0,
            next: !0,
            prev: !0
        };
    oe.fn.extend({
        has: function(e) {
            var t = oe(e, this),
                n = t.length;
            return this.filter(function() {
                for (var e = 0; n > e; e++)
                    if (oe.contains(this, t[e])) return !0
            })
        },
        closest: function(e, t) {
            for (var n, i = 0, r = this.length, o = [], s = pe.test(e) || "string" != typeof e ? oe(e, t || this.context) : 0; r > i; i++)
                for (n = this[i]; n && n !== t; n = n.parentNode)
                    if (n.nodeType < 11 && (s ? s.index(n) > -1 : 1 === n.nodeType && oe.find.matchesSelector(n, e))) {
                        o.push(n);
                        break
                    }
            return this.pushStack(o.length > 1 ? oe.uniqueSort(o) : o)
        },
        index: function(e) {
            return e ? "string" == typeof e ? Z.call(oe(e), this[0]) : Z.call(this, e.jquery ? e[0] : e) : this[0] && this[0].parentNode ? this.first().prevAll().length : -1
        },
        add: function(e, t) {
            return this.pushStack(oe.uniqueSort(oe.merge(this.get(), oe(e, t))))
        },
        addBack: function(e) {
            return this.add(null == e ? this.prevObject : this.prevObject.filter(e))
        }
    }), oe.each({
        parent: function(e) {
            var t = e.parentNode;
            return t && 11 !== t.nodeType ? t : null
        },
        parents: function(e) {
            return fe(e, "parentNode")
        },
        parentsUntil: function(e, t, n) {
            return fe(e, "parentNode", n)
        },
        next: function(e) {
            return r(e, "nextSibling")
        },
        prev: function(e) {
            return r(e, "previousSibling")
        },
        nextAll: function(e) {
            return fe(e, "nextSibling")
        },
        prevAll: function(e) {
            return fe(e, "previousSibling")
        },
        nextUntil: function(e, t, n) {
            return fe(e, "nextSibling", n)
        },
        prevUntil: function(e, t, n) {
            return fe(e, "previousSibling", n)
        },
        siblings: function(e) {
            return de((e.parentNode || {}).firstChild, e)
        },
        children: function(e) {
            return de(e.firstChild)
        },
        contents: function(e) {
            return e.contentDocument || oe.merge([], e.childNodes)
        }
    }, function(e, t) {
        oe.fn[e] = function(n, i) {
            var r = oe.map(this, t, n);
            return "Until" !== e.slice(-5) && (i = n), i && "string" == typeof i && (r = oe.filter(i, r)), this.length > 1 && (we[e] || oe.uniqueSort(r), xe.test(e) && r.reverse()), this.pushStack(r)
        }
    });
    var be = /\S+/g;
    oe.Callbacks = function(e) {
        e = "string" == typeof e ? o(e) : oe.extend({}, e);
        var t, n, i, r, s = [],
            a = [],
            u = -1,
            l = function() {
                for (r = e.once, i = t = !0; a.length; u = -1)
                    for (n = a.shift(); ++u < s.length;) s[u].apply(n[0], n[1]) === !1 && e.stopOnFalse && (u = s.length, n = !1);
                e.memory || (n = !1), t = !1, r && (s = n ? [] : "")
            },
            c = {
                add: function() {
                    return s && (n && !t && (u = s.length - 1, a.push(n)), function i(t) {
                        oe.each(t, function(t, n) {
                            oe.isFunction(n) ? e.unique && c.has(n) || s.push(n) : n && n.length && "string" !== oe.type(n) && i(n)
                        })
                    }(arguments), n && !t && l()), this
                },
                remove: function() {
                    return oe.each(arguments, function(e, t) {
                        for (var n;
                            (n = oe.inArray(t, s, n)) > -1;) s.splice(n, 1), u >= n && u--
                    }), this
                },
                has: function(e) {
                    return e ? oe.inArray(e, s) > -1 : s.length > 0
                },
                empty: function() {
                    return s && (s = []), this
                },
                disable: function() {
                    return r = a = [], s = n = "", this
                },
                disabled: function() {
                    return !s
                },
                lock: function() {
                    return r = a = [], n || (s = n = ""), this
                },
                locked: function() {
                    return !!r
                },
                fireWith: function(e, n) {
                    return r || (n = n || [], n = [e, n.slice ? n.slice() : n], a.push(n), t || l()), this
                },
                fire: function() {
                    return c.fireWith(this, arguments), this
                },
                fired: function() {
                    return !!i
                }
            };
        return c
    }, oe.extend({
        Deferred: function(e) {
            var t = [
                    ["resolve", "done", oe.Callbacks("once memory"), "resolved"],
                    ["reject", "fail", oe.Callbacks("once memory"), "rejected"],
                    ["notify", "progress", oe.Callbacks("memory")]
                ],
                n = "pending",
                i = {
                    state: function() {
                        return n
                    },
                    always: function() {
                        return r.done(arguments).fail(arguments), this
                    },
                    then: function() {
                        var e = arguments;
                        return oe.Deferred(function(n) {
                            oe.each(t, function(t, o) {
                                var s = oe.isFunction(e[t]) && e[t];
                                r[o[1]](function() {
                                    var e = s && s.apply(this, arguments);
                                    e && oe.isFunction(e.promise) ? e.promise().progress(n.notify).done(n.resolve).fail(n.reject) : n[o[0] + "With"](this === i ? n.promise() : this, s ? [e] : arguments)
                                })
                            }), e = null
                        }).promise()
                    },
                    promise: function(e) {
                        return null != e ? oe.extend(e, i) : i
                    }
                },
                r = {};
            return i.pipe = i.then, oe.each(t, function(e, o) {
                var s = o[2],
                    a = o[3];
                i[o[1]] = s.add, a && s.add(function() {
                    n = a
                }, t[1 ^ e][2].disable, t[2][2].lock), r[o[0]] = function() {
                    return r[o[0] + "With"](this === r ? i : this, arguments), this
                }, r[o[0] + "With"] = s.fireWith
            }), i.promise(r), e && e.call(r, r), r
        },
        when: function(e) {
            var t, n, i, r = 0,
                o = Y.call(arguments),
                s = o.length,
                a = 1 !== s || e && oe.isFunction(e.promise) ? s : 0,
                u = 1 === a ? e : oe.Deferred(),
                l = function(e, n, i) {
                    return function(r) {
                        n[e] = this, i[e] = arguments.length > 1 ? Y.call(arguments) : r, i === t ? u.notifyWith(n, i) : --a || u.resolveWith(n, i)
                    }
                };
            if (s > 1)
                for (t = new Array(s), n = new Array(s), i = new Array(s); s > r; r++) o[r] && oe.isFunction(o[r].promise) ? o[r].promise().progress(l(r, n, t)).done(l(r, i, o)).fail(u.reject) : --a;
            return a || u.resolveWith(i, o), u.promise()
        }
    });
    var Te;
    oe.fn.ready = function(e) {
        return oe.ready.promise().done(e), this
    }, oe.extend({
        isReady: !1,
        readyWait: 1,
        holdReady: function(e) {
            e ? oe.readyWait++ : oe.ready(!0)
        },
        ready: function(e) {
            (e === !0 ? --oe.readyWait : oe.isReady) || (oe.isReady = !0, e !== !0 && --oe.readyWait > 0 || (Te.resolveWith(J, [oe]), oe.fn.triggerHandler && (oe(J).triggerHandler("ready"), oe(J).off("ready"))))
        }
    }), oe.ready.promise = function(t) {
        return Te || (Te = oe.Deferred(), "complete" === J.readyState || "loading" !== J.readyState && !J.documentElement.doScroll ? e.setTimeout(oe.ready) : (J.addEventListener("DOMContentLoaded", s), e.addEventListener("load", s))), Te.promise(t)
    }, oe.ready.promise();
    var Ce = function(e, t, n, i, r, o, s) {
            var a = 0,
                u = e.length,
                l = null == n;
            if ("object" === oe.type(n)) {
                r = !0;
                for (a in n) Ce(e, t, a, n[a], !0, o, s)
            } else if (void 0 !== i && (r = !0, oe.isFunction(i) || (s = !0), l && (s ? (t.call(e, i), t = null) : (l = t, t = function(e, t, n) {
                    return l.call(oe(e), n)
                })), t))
                for (; u > a; a++) t(e[a], n, s ? i : i.call(e[a], a, t(e[a], n)));
            return r ? e : l ? t.call(e) : u ? t(e[0], n) : o
        },
        ke = function(e) {
            return 1 === e.nodeType || 9 === e.nodeType || !+e.nodeType
        };
    a.uid = 1, a.prototype = {
        register: function(e, t) {
            var n = t || {};
            return e.nodeType ? e[this.expando] = n : Object.defineProperty(e, this.expando, {
                value: n,
                writable: !0,
                configurable: !0
            }), e[this.expando]
        },
        cache: function(e) {
            if (!ke(e)) return {};
            var t = e[this.expando];
            return t || (t = {}, ke(e) && (e.nodeType ? e[this.expando] = t : Object.defineProperty(e, this.expando, {
                value: t,
                configurable: !0
            }))), t
        },
        set: function(e, t, n) {
            var i, r = this.cache(e);
            if ("string" == typeof t) r[t] = n;
            else
                for (i in t) r[i] = t[i];
            return r
        },
        get: function(e, t) {
            return void 0 === t ? this.cache(e) : e[this.expando] && e[this.expando][t]
        },
        access: function(e, t, n) {
            var i;
            return void 0 === t || t && "string" == typeof t && void 0 === n ? (i = this.get(e, t), void 0 !== i ? i : this.get(e, oe.camelCase(t))) : (this.set(e, t, n), void 0 !== n ? n : t)
        },
        remove: function(e, t) {
            var n, i, r, o = e[this.expando];
            if (void 0 !== o) {
                if (void 0 === t) this.register(e);
                else {
                    oe.isArray(t) ? i = t.concat(t.map(oe.camelCase)) : (r = oe.camelCase(t), t in o ? i = [t, r] : (i = r, i = i in o ? [i] : i.match(be) || [])), n = i.length;
                    for (; n--;) delete o[i[n]]
                }(void 0 === t || oe.isEmptyObject(o)) && (e.nodeType ? e[this.expando] = void 0 : delete e[this.expando])
            }
        },
        hasData: function(e) {
            var t = e[this.expando];
            return void 0 !== t && !oe.isEmptyObject(t)
        }
    };
    var Ee = new a,
        Se = new a,
        Ne = /^(?:\{[\w\W]*\}|\[[\w\W]*\])$/,
        Ae = /[A-Z]/g;
    oe.extend({
        hasData: function(e) {
            return Se.hasData(e) || Ee.hasData(e)
        },
        data: function(e, t, n) {
            return Se.access(e, t, n)
        },
        removeData: function(e, t) {
            Se.remove(e, t)
        },
        _data: function(e, t, n) {
            return Ee.access(e, t, n)
        },
        _removeData: function(e, t) {
            Ee.remove(e, t)
        }
    }), oe.fn.extend({
        data: function(e, t) {
            var n, i, r, o = this[0],
                s = o && o.attributes;
            if (void 0 === e) {
                if (this.length && (r = Se.get(o), 1 === o.nodeType && !Ee.get(o, "hasDataAttrs"))) {
                    for (n = s.length; n--;) s[n] && (i = s[n].name, 0 === i.indexOf("data-") && (i = oe.camelCase(i.slice(5)), u(o, i, r[i])));
                    Ee.set(o, "hasDataAttrs", !0)
                }
                return r
            }
            return "object" == typeof e ? this.each(function() {
                Se.set(this, e)
            }) : Ce(this, function(t) {
                var n, i;
                if (o && void 0 === t) {
                    if (n = Se.get(o, e) || Se.get(o, e.replace(Ae, "-$&").toLowerCase()), void 0 !== n) return n;
                    if (i = oe.camelCase(e), n = Se.get(o, i), void 0 !== n) return n;
                    if (n = u(o, i, void 0), void 0 !== n) return n
                } else i = oe.camelCase(e), this.each(function() {
                    var n = Se.get(this, i);
                    Se.set(this, i, t), e.indexOf("-") > -1 && void 0 !== n && Se.set(this, e, t)
                })
            }, null, t, arguments.length > 1, null, !0)
        },
        removeData: function(e) {
            return this.each(function() {
                Se.remove(this, e)
            })
        }
    }), oe.extend({
        queue: function(e, t, n) {
            var i;
            return e ? (t = (t || "fx") + "queue", i = Ee.get(e, t), n && (!i || oe.isArray(n) ? i = Ee.access(e, t, oe.makeArray(n)) : i.push(n)), i || []) : void 0
        },
        dequeue: function(e, t) {
            t = t || "fx";
            var n = oe.queue(e, t),
                i = n.length,
                r = n.shift(),
                o = oe._queueHooks(e, t),
                s = function() {
                    oe.dequeue(e, t)
                };
            "inprogress" === r && (r = n.shift(), i--), r && ("fx" === t && n.unshift("inprogress"), delete o.stop, r.call(e, s, o)), !i && o && o.empty.fire()
        },
        _queueHooks: function(e, t) {
            var n = t + "queueHooks";
            return Ee.get(e, n) || Ee.access(e, n, {
                empty: oe.Callbacks("once memory").add(function() {
                    Ee.remove(e, [t + "queue", n])
                })
            })
        }
    }), oe.fn.extend({
        queue: function(e, t) {
            var n = 2;
            return "string" != typeof e && (t = e, e = "fx", n--), arguments.length < n ? oe.queue(this[0], e) : void 0 === t ? this : this.each(function() {
                var n = oe.queue(this, e, t);
                oe._queueHooks(this, e), "fx" === e && "inprogress" !== n[0] && oe.dequeue(this, e)
            })
        },
        dequeue: function(e) {
            return this.each(function() {
                oe.dequeue(this, e)
            })
        },
        clearQueue: function(e) {
            return this.queue(e || "fx", [])
        },
        promise: function(e, t) {
            var n, i = 1,
                r = oe.Deferred(),
                o = this,
                s = this.length,
                a = function() {
                    --i || r.resolveWith(o, [o])
                };
            for ("string" != typeof e && (t = e, e = void 0), e = e || "fx"; s--;) n = Ee.get(o[s], e + "queueHooks"), n && n.empty && (i++, n.empty.add(a));
            return a(), r.promise(t)
        }
    });
    var je = /[+-]?(?:\d*\.|)\d+(?:[eE][+-]?\d+|)/.source,
        De = new RegExp("^(?:([+-])=|)(" + je + ")([a-z%]*)$", "i"),
        Le = ["Top", "Right", "Bottom", "Left"],
        Oe = function(e, t) {
            return e = t || e, "none" === oe.css(e, "display") || !oe.contains(e.ownerDocument, e)
        },
        qe = /^(?:checkbox|radio)$/i,
        Fe = /<([\w:-]+)/,
        $e = /^$|\/(?:java|ecma)script/i,
        We = {
            option: [1, "<select multiple='multiple'>", "</select>"],
            thead: [1, "<table>", "</table>"],
            col: [2, "<table><colgroup>", "</colgroup></table>"],
            tr: [2, "<table><tbody>", "</tbody></table>"],
            td: [3, "<table><tbody><tr>", "</tr></tbody></table>"],
            _default: [0, "", ""]
        };
    We.optgroup = We.option, We.tbody = We.tfoot = We.colgroup = We.caption = We.thead, We.th = We.td;
    var He = /<|&#?\w+;/;
    ! function() {
        var e = J.createDocumentFragment(),
            t = e.appendChild(J.createElement("div")),
            n = J.createElement("input");
        n.setAttribute("type", "radio"), n.setAttribute("checked", "checked"), n.setAttribute("name", "t"), t.appendChild(n), ie.checkClone = t.cloneNode(!0).cloneNode(!0).lastChild.checked, t.innerHTML = "<textarea>x</textarea>", ie.noCloneChecked = !!t.cloneNode(!0).lastChild.defaultValue
    }();
    var Pe = /^key/,
        Me = /^(?:mouse|pointer|contextmenu|drag|drop)|click/,
        Ie = /^([^.]*)(?:\.(.+)|)/;
    oe.event = {
        global: {},
        add: function(e, t, n, i, r) {
            var o, s, a, u, l, c, f, d, p, h, g, m = Ee.get(e);
            if (m)
                for (n.handler && (o = n, n = o.handler, r = o.selector), n.guid || (n.guid = oe.guid++), (u = m.events) || (u = m.events = {}), (s = m.handle) || (s = m.handle = function(t) {
                        return "undefined" != typeof oe && oe.event.triggered !== t.type ? oe.event.dispatch.apply(e, arguments) : void 0
                    }), t = (t || "").match(be) || [""], l = t.length; l--;) a = Ie.exec(t[l]) || [], p = g = a[1], h = (a[2] || "").split(".").sort(), p && (f = oe.event.special[p] || {}, p = (r ? f.delegateType : f.bindType) || p, f = oe.event.special[p] || {}, c = oe.extend({
                    type: p,
                    origType: g,
                    data: i,
                    handler: n,
                    guid: n.guid,
                    selector: r,
                    needsContext: r && oe.expr.match.needsContext.test(r),
                    namespace: h.join(".")
                }, o), (d = u[p]) || (d = u[p] = [], d.delegateCount = 0, f.setup && f.setup.call(e, i, h, s) !== !1 || e.addEventListener && e.addEventListener(p, s)), f.add && (f.add.call(e, c), c.handler.guid || (c.handler.guid = n.guid)), r ? d.splice(d.delegateCount++, 0, c) : d.push(c), oe.event.global[p] = !0)
        },
        remove: function(e, t, n, i, r) {
            var o, s, a, u, l, c, f, d, p, h, g, m = Ee.hasData(e) && Ee.get(e);
            if (m && (u = m.events)) {
                for (t = (t || "").match(be) || [""], l = t.length; l--;)
                    if (a = Ie.exec(t[l]) || [], p = g = a[1], h = (a[2] || "").split(".").sort(),
                        p) {
                        for (f = oe.event.special[p] || {}, p = (i ? f.delegateType : f.bindType) || p, d = u[p] || [], a = a[2] && new RegExp("(^|\\.)" + h.join("\\.(?:.*\\.|)") + "(\\.|$)"), s = o = d.length; o--;) c = d[o], !r && g !== c.origType || n && n.guid !== c.guid || a && !a.test(c.namespace) || i && i !== c.selector && ("**" !== i || !c.selector) || (d.splice(o, 1), c.selector && d.delegateCount--, f.remove && f.remove.call(e, c));
                        s && !d.length && (f.teardown && f.teardown.call(e, h, m.handle) !== !1 || oe.removeEvent(e, p, m.handle), delete u[p])
                    } else
                        for (p in u) oe.event.remove(e, p + t[l], n, i, !0);
                oe.isEmptyObject(u) && Ee.remove(e, "handle events")
            }
        },
        dispatch: function(e) {
            e = oe.event.fix(e);
            var t, n, i, r, o, s = [],
                a = Y.call(arguments),
                u = (Ee.get(this, "events") || {})[e.type] || [],
                l = oe.event.special[e.type] || {};
            if (a[0] = e, e.delegateTarget = this, !l.preDispatch || l.preDispatch.call(this, e) !== !1) {
                for (s = oe.event.handlers.call(this, e, u), t = 0;
                    (r = s[t++]) && !e.isPropagationStopped();)
                    for (e.currentTarget = r.elem, n = 0;
                        (o = r.handlers[n++]) && !e.isImmediatePropagationStopped();) e.rnamespace && !e.rnamespace.test(o.namespace) || (e.handleObj = o, e.data = o.data, i = ((oe.event.special[o.origType] || {}).handle || o.handler).apply(r.elem, a), void 0 !== i && (e.result = i) === !1 && (e.preventDefault(), e.stopPropagation()));
                return l.postDispatch && l.postDispatch.call(this, e), e.result
            }
        },
        handlers: function(e, t) {
            var n, i, r, o, s = [],
                a = t.delegateCount,
                u = e.target;
            if (a && u.nodeType && ("click" !== e.type || isNaN(e.button) || e.button < 1))
                for (; u !== this; u = u.parentNode || this)
                    if (1 === u.nodeType && (u.disabled !== !0 || "click" !== e.type)) {
                        for (i = [], n = 0; a > n; n++) o = t[n], r = o.selector + " ", void 0 === i[r] && (i[r] = o.needsContext ? oe(r, this).index(u) > -1 : oe.find(r, this, null, [u]).length), i[r] && i.push(o);
                        i.length && s.push({
                            elem: u,
                            handlers: i
                        })
                    }
            return a < t.length && s.push({
                elem: this,
                handlers: t.slice(a)
            }), s
        },
        props: "altKey bubbles cancelable ctrlKey currentTarget detail eventPhase metaKey relatedTarget shiftKey target timeStamp view which".split(" "),
        fixHooks: {},
        keyHooks: {
            props: "char charCode key keyCode".split(" "),
            filter: function(e, t) {
                return null == e.which && (e.which = null != t.charCode ? t.charCode : t.keyCode), e
            }
        },
        mouseHooks: {
            props: "button buttons clientX clientY offsetX offsetY pageX pageY screenX screenY toElement".split(" "),
            filter: function(e, t) {
                var n, i, r, o = t.button;
                return null == e.pageX && null != t.clientX && (n = e.target.ownerDocument || J, i = n.documentElement, r = n.body, e.pageX = t.clientX + (i && i.scrollLeft || r && r.scrollLeft || 0) - (i && i.clientLeft || r && r.clientLeft || 0), e.pageY = t.clientY + (i && i.scrollTop || r && r.scrollTop || 0) - (i && i.clientTop || r && r.clientTop || 0)), e.which || void 0 === o || (e.which = 1 & o ? 1 : 2 & o ? 3 : 4 & o ? 2 : 0), e
            }
        },
        fix: function(e) {
            if (e[oe.expando]) return e;
            var t, n, i, r = e.type,
                o = e,
                s = this.fixHooks[r];
            for (s || (this.fixHooks[r] = s = Me.test(r) ? this.mouseHooks : Pe.test(r) ? this.keyHooks : {}), i = s.props ? this.props.concat(s.props) : this.props, e = new oe.Event(o), t = i.length; t--;) n = i[t], e[n] = o[n];
            return e.target || (e.target = J), 3 === e.target.nodeType && (e.target = e.target.parentNode), s.filter ? s.filter(e, o) : e
        },
        special: {
            load: {
                noBubble: !0
            },
            focus: {
                trigger: function() {
                    return this !== g() && this.focus ? (this.focus(), !1) : void 0
                },
                delegateType: "focusin"
            },
            blur: {
                trigger: function() {
                    return this === g() && this.blur ? (this.blur(), !1) : void 0
                },
                delegateType: "focusout"
            },
            click: {
                trigger: function() {
                    return "checkbox" === this.type && this.click && oe.nodeName(this, "input") ? (this.click(), !1) : void 0
                },
                _default: function(e) {
                    return oe.nodeName(e.target, "a")
                }
            },
            beforeunload: {
                postDispatch: function(e) {
                    void 0 !== e.result && e.originalEvent && (e.originalEvent.returnValue = e.result)
                }
            }
        }
    }, oe.removeEvent = function(e, t, n) {
        e.removeEventListener && e.removeEventListener(t, n)
    }, oe.Event = function(e, t) {
        return this instanceof oe.Event ? (e && e.type ? (this.originalEvent = e, this.type = e.type, this.isDefaultPrevented = e.defaultPrevented || void 0 === e.defaultPrevented && e.returnValue === !1 ? p : h) : this.type = e, t && oe.extend(this, t), this.timeStamp = e && e.timeStamp || oe.now(), void(this[oe.expando] = !0)) : new oe.Event(e, t)
    }, oe.Event.prototype = {
        constructor: oe.Event,
        isDefaultPrevented: h,
        isPropagationStopped: h,
        isImmediatePropagationStopped: h,
        isSimulated: !1,
        preventDefault: function() {
            var e = this.originalEvent;
            this.isDefaultPrevented = p, e && !this.isSimulated && e.preventDefault()
        },
        stopPropagation: function() {
            var e = this.originalEvent;
            this.isPropagationStopped = p, e && !this.isSimulated && e.stopPropagation()
        },
        stopImmediatePropagation: function() {
            var e = this.originalEvent;
            this.isImmediatePropagationStopped = p, e && !this.isSimulated && e.stopImmediatePropagation(), this.stopPropagation()
        }
    }, oe.each({
        mouseenter: "mouseover",
        mouseleave: "mouseout",
        pointerenter: "pointerover",
        pointerleave: "pointerout"
    }, function(e, t) {
        oe.event.special[e] = {
            delegateType: t,
            bindType: t,
            handle: function(e) {
                var n, i = this,
                    r = e.relatedTarget,
                    o = e.handleObj;
                return r && (r === i || oe.contains(i, r)) || (e.type = o.origType, n = o.handler.apply(this, arguments), e.type = t), n
            }
        }
    }), oe.fn.extend({
        on: function(e, t, n, i) {
            return m(this, e, t, n, i)
        },
        one: function(e, t, n, i) {
            return m(this, e, t, n, i, 1)
        },
        off: function(e, t, n) {
            var i, r;
            if (e && e.preventDefault && e.handleObj) return i = e.handleObj, oe(e.delegateTarget).off(i.namespace ? i.origType + "." + i.namespace : i.origType, i.selector, i.handler), this;
            if ("object" == typeof e) {
                for (r in e) this.off(r, t, e[r]);
                return this
            }
            return t !== !1 && "function" != typeof t || (n = t, t = void 0), n === !1 && (n = h), this.each(function() {
                oe.event.remove(this, e, n, t)
            })
        }
    });
    var Be = /<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:-]+)[^>]*)\/>/gi,
        Re = /<script|<style|<link/i,
        _e = /checked\s*(?:[^=]|=\s*.checked.)/i,
        ze = /^true\/(.*)/,
        Xe = /^\s*<!(?:\[CDATA\[|--)|(?:\]\]|--)>\s*$/g;
    oe.extend({
        htmlPrefilter: function(e) {
            return e.replace(Be, "<$1></$2>")
        },
        clone: function(e, t, n) {
            var i, r, o, s, a = e.cloneNode(!0),
                u = oe.contains(e.ownerDocument, e);
            if (!(ie.noCloneChecked || 1 !== e.nodeType && 11 !== e.nodeType || oe.isXMLDoc(e)))
                for (s = c(a), o = c(e), i = 0, r = o.length; r > i; i++) b(o[i], s[i]);
            if (t)
                if (n)
                    for (o = o || c(e), s = s || c(a), i = 0, r = o.length; r > i; i++) w(o[i], s[i]);
                else w(e, a);
            return s = c(a, "script"), s.length > 0 && f(s, !u && c(e, "script")), a
        },
        cleanData: function(e) {
            for (var t, n, i, r = oe.event.special, o = 0; void 0 !== (n = e[o]); o++)
                if (ke(n)) {
                    if (t = n[Ee.expando]) {
                        if (t.events)
                            for (i in t.events) r[i] ? oe.event.remove(n, i) : oe.removeEvent(n, i, t.handle);
                        n[Ee.expando] = void 0
                    }
                    n[Se.expando] && (n[Se.expando] = void 0)
                }
        }
    }), oe.fn.extend({
        domManip: T,
        detach: function(e) {
            return C(this, e, !0)
        },
        remove: function(e) {
            return C(this, e)
        },
        text: function(e) {
            return Ce(this, function(e) {
                return void 0 === e ? oe.text(this) : this.empty().each(function() {
                    1 !== this.nodeType && 11 !== this.nodeType && 9 !== this.nodeType || (this.textContent = e)
                })
            }, null, e, arguments.length)
        },
        append: function() {
            return T(this, arguments, function(e) {
                if (1 === this.nodeType || 11 === this.nodeType || 9 === this.nodeType) {
                    var t = v(this, e);
                    t.appendChild(e)
                }
            })
        },
        prepend: function() {
            return T(this, arguments, function(e) {
                if (1 === this.nodeType || 11 === this.nodeType || 9 === this.nodeType) {
                    var t = v(this, e);
                    t.insertBefore(e, t.firstChild)
                }
            })
        },
        before: function() {
            return T(this, arguments, function(e) {
                this.parentNode && this.parentNode.insertBefore(e, this)
            })
        },
        after: function() {
            return T(this, arguments, function(e) {
                this.parentNode && this.parentNode.insertBefore(e, this.nextSibling)
            })
        },
        empty: function() {
            for (var e, t = 0; null != (e = this[t]); t++) 1 === e.nodeType && (oe.cleanData(c(e, !1)), e.textContent = "");
            return this
        },
        clone: function(e, t) {
            return e = null == e ? !1 : e, t = null == t ? e : t, this.map(function() {
                return oe.clone(this, e, t)
            })
        },
        html: function(e) {
            return Ce(this, function(e) {
                var t = this[0] || {},
                    n = 0,
                    i = this.length;
                if (void 0 === e && 1 === t.nodeType) return t.innerHTML;
                if ("string" == typeof e && !Re.test(e) && !We[(Fe.exec(e) || ["", ""])[1].toLowerCase()]) {
                    e = oe.htmlPrefilter(e);
                    try {
                        for (; i > n; n++) t = this[n] || {}, 1 === t.nodeType && (oe.cleanData(c(t, !1)), t.innerHTML = e);
                        t = 0
                    } catch (r) {}
                }
                t && this.empty().append(e)
            }, null, e, arguments.length)
        },
        replaceWith: function() {
            var e = [];
            return T(this, arguments, function(t) {
                var n = this.parentNode;
                oe.inArray(this, e) < 0 && (oe.cleanData(c(this)), n && n.replaceChild(t, this))
            }, e)
        }
    }), oe.each({
        appendTo: "append",
        prependTo: "prepend",
        insertBefore: "before",
        insertAfter: "after",
        replaceAll: "replaceWith"
    }, function(e, t) {
        oe.fn[e] = function(e) {
            for (var n, i = [], r = oe(e), o = r.length - 1, s = 0; o >= s; s++) n = s === o ? this : this.clone(!0), oe(r[s])[t](n), Q.apply(i, n.get());
            return this.pushStack(i)
        }
    });
    var Ue, Ve = {
            HTML: "block",
            BODY: "block"
        },
        Ge = /^margin/,
        Je = new RegExp("^(" + je + ")(?!px)[a-z%]+$", "i"),
        Ye = function(t) {
            var n = t.ownerDocument.defaultView;
            return n && n.opener || (n = e), n.getComputedStyle(t)
        },
        Ke = function(e, t, n, i) {
            var r, o, s = {};
            for (o in t) s[o] = e.style[o], e.style[o] = t[o];
            r = n.apply(e, i || []);
            for (o in t) e.style[o] = s[o];
            return r
        },
        Qe = J.documentElement;
    ! function() {
        function t() {
            a.style.cssText = "-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;position:relative;display:block;margin:auto;border:1px;padding:1px;top:1%;width:50%", a.innerHTML = "", Qe.appendChild(s);
            var t = e.getComputedStyle(a);
            n = "1%" !== t.top, o = "2px" === t.marginLeft, i = "4px" === t.width, a.style.marginRight = "50%", r = "4px" === t.marginRight, Qe.removeChild(s)
        }
        var n, i, r, o, s = J.createElement("div"),
            a = J.createElement("div");
        a.style && (a.style.backgroundClip = "content-box", a.cloneNode(!0).style.backgroundClip = "", ie.clearCloneStyle = "content-box" === a.style.backgroundClip, s.style.cssText = "border:0;width:8px;height:0;top:0;left:-9999px;padding:0;margin-top:1px;position:absolute", s.appendChild(a), oe.extend(ie, {
            pixelPosition: function() {
                return t(), n
            },
            boxSizingReliable: function() {
                return null == i && t(), i
            },
            pixelMarginRight: function() {
                return null == i && t(), r
            },
            reliableMarginLeft: function() {
                return null == i && t(), o
            },
            reliableMarginRight: function() {
                var t, n = a.appendChild(J.createElement("div"));
                return n.style.cssText = a.style.cssText = "-webkit-box-sizing:content-box;box-sizing:content-box;display:block;margin:0;border:0;padding:0", n.style.marginRight = n.style.width = "0", a.style.width = "1px", Qe.appendChild(s), t = !parseFloat(e.getComputedStyle(n).marginRight), Qe.removeChild(s), a.removeChild(n), t
            }
        }))
    }();
    var Ze = /^(none|table(?!-c[ea]).+)/,
        et = {
            position: "absolute",
            visibility: "hidden",
            display: "block"
        },
        tt = {
            letterSpacing: "0",
            fontWeight: "400"
        },
        nt = ["Webkit", "O", "Moz", "ms"],
        it = J.createElement("div").style;
    oe.extend({
        cssHooks: {
            opacity: {
                get: function(e, t) {
                    if (t) {
                        var n = S(e, "opacity");
                        return "" === n ? "1" : n
                    }
                }
            }
        },
        cssNumber: {
            animationIterationCount: !0,
            columnCount: !0,
            fillOpacity: !0,
            flexGrow: !0,
            flexShrink: !0,
            fontWeight: !0,
            lineHeight: !0,
            opacity: !0,
            order: !0,
            orphans: !0,
            widows: !0,
            zIndex: !0,
            zoom: !0
        },
        cssProps: {
            "float": "cssFloat"
        },
        style: function(e, t, n, i) {
            if (e && 3 !== e.nodeType && 8 !== e.nodeType && e.style) {
                var r, o, s, a = oe.camelCase(t),
                    u = e.style;
                return t = oe.cssProps[a] || (oe.cssProps[a] = A(a) || a), s = oe.cssHooks[t] || oe.cssHooks[a], void 0 === n ? s && "get" in s && void 0 !== (r = s.get(e, !1, i)) ? r : u[t] : (o = typeof n, "string" === o && (r = De.exec(n)) && r[1] && (n = l(e, t, r), o = "number"), null != n && n === n && ("number" === o && (n += r && r[3] || (oe.cssNumber[a] ? "" : "px")), ie.clearCloneStyle || "" !== n || 0 !== t.indexOf("background") || (u[t] = "inherit"), s && "set" in s && void 0 === (n = s.set(e, n, i)) || (u[t] = n)), void 0)
            }
        },
        css: function(e, t, n, i) {
            var r, o, s, a = oe.camelCase(t);
            return t = oe.cssProps[a] || (oe.cssProps[a] = A(a) || a), s = oe.cssHooks[t] || oe.cssHooks[a], s && "get" in s && (r = s.get(e, !0, n)), void 0 === r && (r = S(e, t, i)), "normal" === r && t in tt && (r = tt[t]), "" === n || n ? (o = parseFloat(r), n === !0 || isFinite(o) ? o || 0 : r) : r
        }
    }), oe.each(["height", "width"], function(e, t) {
        oe.cssHooks[t] = {
            get: function(e, n, i) {
                return n ? Ze.test(oe.css(e, "display")) && 0 === e.offsetWidth ? Ke(e, et, function() {
                    return L(e, t, i)
                }) : L(e, t, i) : void 0
            },
            set: function(e, n, i) {
                var r, o = i && Ye(e),
                    s = i && D(e, t, i, "border-box" === oe.css(e, "boxSizing", !1, o), o);
                return s && (r = De.exec(n)) && "px" !== (r[3] || "px") && (e.style[t] = n, n = oe.css(e, t)), j(e, n, s)
            }
        }
    }), oe.cssHooks.marginLeft = N(ie.reliableMarginLeft, function(e, t) {
        return t ? (parseFloat(S(e, "marginLeft")) || e.getBoundingClientRect().left - Ke(e, {
            marginLeft: 0
        }, function() {
            return e.getBoundingClientRect().left
        })) + "px" : void 0
    }), oe.cssHooks.marginRight = N(ie.reliableMarginRight, function(e, t) {
        return t ? Ke(e, {
            display: "inline-block"
        }, S, [e, "marginRight"]) : void 0
    }), oe.each({
        margin: "",
        padding: "",
        border: "Width"
    }, function(e, t) {
        oe.cssHooks[e + t] = {
            expand: function(n) {
                for (var i = 0, r = {}, o = "string" == typeof n ? n.split(" ") : [n]; 4 > i; i++) r[e + Le[i] + t] = o[i] || o[i - 2] || o[0];
                return r
            }
        }, Ge.test(e) || (oe.cssHooks[e + t].set = j)
    }), oe.fn.extend({
        css: function(e, t) {
            return Ce(this, function(e, t, n) {
                var i, r, o = {},
                    s = 0;
                if (oe.isArray(t)) {
                    for (i = Ye(e), r = t.length; r > s; s++) o[t[s]] = oe.css(e, t[s], !1, i);
                    return o
                }
                return void 0 !== n ? oe.style(e, t, n) : oe.css(e, t)
            }, e, t, arguments.length > 1)
        },
        show: function() {
            return O(this, !0)
        },
        hide: function() {
            return O(this)
        },
        toggle: function(e) {
            return "boolean" == typeof e ? e ? this.show() : this.hide() : this.each(function() {
                Oe(this) ? oe(this).show() : oe(this).hide()
            })
        }
    }), oe.Tween = q, q.prototype = {
        constructor: q,
        init: function(e, t, n, i, r, o) {
            this.elem = e, this.prop = n, this.easing = r || oe.easing._default, this.options = t, this.start = this.now = this.cur(), this.end = i, this.unit = o || (oe.cssNumber[n] ? "" : "px")
        },
        cur: function() {
            var e = q.propHooks[this.prop];
            return e && e.get ? e.get(this) : q.propHooks._default.get(this)
        },
        run: function(e) {
            var t, n = q.propHooks[this.prop];
            return this.options.duration ? this.pos = t = oe.easing[this.easing](e, this.options.duration * e, 0, 1, this.options.duration) : this.pos = t = e, this.now = (this.end - this.start) * t + this.start, this.options.step && this.options.step.call(this.elem, this.now, this), n && n.set ? n.set(this) : q.propHooks._default.set(this), this
        }
    }, q.prototype.init.prototype = q.prototype, q.propHooks = {
        _default: {
            get: function(e) {
                var t;
                return 1 !== e.elem.nodeType || null != e.elem[e.prop] && null == e.elem.style[e.prop] ? e.elem[e.prop] : (t = oe.css(e.elem, e.prop, ""), t && "auto" !== t ? t : 0)
            },
            set: function(e) {
                oe.fx.step[e.prop] ? oe.fx.step[e.prop](e) : 1 !== e.elem.nodeType || null == e.elem.style[oe.cssProps[e.prop]] && !oe.cssHooks[e.prop] ? e.elem[e.prop] = e.now : oe.style(e.elem, e.prop, e.now + e.unit)
            }
        }
    }, q.propHooks.scrollTop = q.propHooks.scrollLeft = {
        set: function(e) {
            e.elem.nodeType && e.elem.parentNode && (e.elem[e.prop] = e.now)
        }
    }, oe.easing = {
        linear: function(e) {
            return e
        },
        swing: function(e) {
            return .5 - Math.cos(e * Math.PI) / 2
        },
        _default: "swing"
    }, oe.fx = q.prototype.init, oe.fx.step = {};
    var rt, ot, st = /^(?:toggle|show|hide)$/,
        at = /queueHooks$/;
    oe.Animation = oe.extend(M, {
            tweeners: {
                "*": [function(e, t) {
                    var n = this.createTween(e, t);
                    return l(n.elem, e, De.exec(t), n), n
                }]
            },
            tweener: function(e, t) {
                oe.isFunction(e) ? (t = e, e = ["*"]) : e = e.match(be);
                for (var n, i = 0, r = e.length; r > i; i++) n = e[i], M.tweeners[n] = M.tweeners[n] || [], M.tweeners[n].unshift(t)
            },
            prefilters: [H],
            prefilter: function(e, t) {
                t ? M.prefilters.unshift(e) : M.prefilters.push(e)
            }
        }), oe.speed = function(e, t, n) {
            var i = e && "object" == typeof e ? oe.extend({}, e) : {
                complete: n || !n && t || oe.isFunction(e) && e,
                duration: e,
                easing: n && t || t && !oe.isFunction(t) && t
            };
            return i.duration = oe.fx.off ? 0 : "number" == typeof i.duration ? i.duration : i.duration in oe.fx.speeds ? oe.fx.speeds[i.duration] : oe.fx.speeds._default, null != i.queue && i.queue !== !0 || (i.queue = "fx"), i.old = i.complete, i.complete = function() {
                oe.isFunction(i.old) && i.old.call(this), i.queue && oe.dequeue(this, i.queue)
            }, i
        }, oe.fn.extend({
            fadeTo: function(e, t, n, i) {
                return this.filter(Oe).css("opacity", 0).show().end().animate({
                    opacity: t
                }, e, n, i)
            },
            animate: function(e, t, n, i) {
                var r = oe.isEmptyObject(e),
                    o = oe.speed(t, n, i),
                    s = function() {
                        var t = M(this, oe.extend({}, e), o);
                        (r || Ee.get(this, "finish")) && t.stop(!0)
                    };
                return s.finish = s, r || o.queue === !1 ? this.each(s) : this.queue(o.queue, s)
            },
            stop: function(e, t, n) {
                var i = function(e) {
                    var t = e.stop;
                    delete e.stop, t(n)
                };
                return "string" != typeof e && (n = t, t = e, e = void 0), t && e !== !1 && this.queue(e || "fx", []), this.each(function() {
                    var t = !0,
                        r = null != e && e + "queueHooks",
                        o = oe.timers,
                        s = Ee.get(this);
                    if (r) s[r] && s[r].stop && i(s[r]);
                    else
                        for (r in s) s[r] && s[r].stop && at.test(r) && i(s[r]);
                    for (r = o.length; r--;) o[r].elem !== this || null != e && o[r].queue !== e || (o[r].anim.stop(n), t = !1, o.splice(r, 1));
                    !t && n || oe.dequeue(this, e)
                })
            },
            finish: function(e) {
                return e !== !1 && (e = e || "fx"), this.each(function() {
                    var t, n = Ee.get(this),
                        i = n[e + "queue"],
                        r = n[e + "queueHooks"],
                        o = oe.timers,
                        s = i ? i.length : 0;
                    for (n.finish = !0, oe.queue(this, e, []), r && r.stop && r.stop.call(this, !0), t = o.length; t--;) o[t].elem === this && o[t].queue === e && (o[t].anim.stop(!0), o.splice(t, 1));
                    for (t = 0; s > t; t++) i[t] && i[t].finish && i[t].finish.call(this);
                    delete n.finish
                })
            }
        }), oe.each(["toggle", "show", "hide"], function(e, t) {
            var n = oe.fn[t];
            oe.fn[t] = function(e, i, r) {
                return null == e || "boolean" == typeof e ? n.apply(this, arguments) : this.animate($(t, !0), e, i, r)
            }
        }), oe.each({
            slideDown: $("show"),
            slideUp: $("hide"),
            slideToggle: $("toggle"),
            fadeIn: {
                opacity: "show"
            },
            fadeOut: {
                opacity: "hide"
            },
            fadeToggle: {
                opacity: "toggle"
            }
        }, function(e, t) {
            oe.fn[e] = function(e, n, i) {
                return this.animate(t, e, n, i)
            }
        }), oe.timers = [], oe.fx.tick = function() {
            var e, t = 0,
                n = oe.timers;
            for (rt = oe.now(); t < n.length; t++) e = n[t], e() || n[t] !== e || n.splice(t--, 1);
            n.length || oe.fx.stop(), rt = void 0
        }, oe.fx.timer = function(e) {
            oe.timers.push(e), e() ? oe.fx.start() : oe.timers.pop()
        }, oe.fx.interval = 13, oe.fx.start = function() {
            ot || (ot = e.setInterval(oe.fx.tick, oe.fx.interval))
        }, oe.fx.stop = function() {
            e.clearInterval(ot), ot = null
        }, oe.fx.speeds = {
            slow: 600,
            fast: 200,
            _default: 400
        }, oe.fn.delay = function(t, n) {
            return t = oe.fx ? oe.fx.speeds[t] || t : t, n = n || "fx", this.queue(n, function(n, i) {
                var r = e.setTimeout(n, t);
                i.stop = function() {
                    e.clearTimeout(r)
                }
            })
        },
        function() {
            var e = J.createElement("input"),
                t = J.createElement("select"),
                n = t.appendChild(J.createElement("option"));
            e.type = "checkbox", ie.checkOn = "" !== e.value, ie.optSelected = n.selected, t.disabled = !0, ie.optDisabled = !n.disabled, e = J.createElement("input"), e.value = "t", e.type = "radio", ie.radioValue = "t" === e.value
        }();
    var ut, lt = oe.expr.attrHandle;
    oe.fn.extend({
        attr: function(e, t) {
            return Ce(this, oe.attr, e, t, arguments.length > 1)
        },
        removeAttr: function(e) {
            return this.each(function() {
                oe.removeAttr(this, e)
            })
        }
    }), oe.extend({
        attr: function(e, t, n) {
            var i, r, o = e.nodeType;
            if (3 !== o && 8 !== o && 2 !== o) return "undefined" == typeof e.getAttribute ? oe.prop(e, t, n) : (1 === o && oe.isXMLDoc(e) || (t = t.toLowerCase(), r = oe.attrHooks[t] || (oe.expr.match.bool.test(t) ? ut : void 0)), void 0 !== n ? null === n ? void oe.removeAttr(e, t) : r && "set" in r && void 0 !== (i = r.set(e, n, t)) ? i : (e.setAttribute(t, n + ""), n) : r && "get" in r && null !== (i = r.get(e, t)) ? i : (i = oe.find.attr(e, t), null == i ? void 0 : i))
        },
        attrHooks: {
            type: {
                set: function(e, t) {
                    if (!ie.radioValue && "radio" === t && oe.nodeName(e, "input")) {
                        var n = e.value;
                        return e.setAttribute("type", t), n && (e.value = n), t
                    }
                }
            }
        },
        removeAttr: function(e, t) {
            var n, i, r = 0,
                o = t && t.match(be);
            if (o && 1 === e.nodeType)
                for (; n = o[r++];) i = oe.propFix[n] || n, oe.expr.match.bool.test(n) && (e[i] = !1), e.removeAttribute(n)
        }
    }), ut = {
        set: function(e, t, n) {
            return t === !1 ? oe.removeAttr(e, n) : e.setAttribute(n, n), n
        }
    }, oe.each(oe.expr.match.bool.source.match(/\w+/g), function(e, t) {
        var n = lt[t] || oe.find.attr;
        lt[t] = function(e, t, i) {
            var r, o;
            return i || (o = lt[t], lt[t] = r, r = null != n(e, t, i) ? t.toLowerCase() : null, lt[t] = o), r
        }
    });
    var ct = /^(?:input|select|textarea|button)$/i,
        ft = /^(?:a|area)$/i;
    oe.fn.extend({
        prop: function(e, t) {
            return Ce(this, oe.prop, e, t, arguments.length > 1)
        },
        removeProp: function(e) {
            return this.each(function() {
                delete this[oe.propFix[e] || e]
            })
        }
    }), oe.extend({
        prop: function(e, t, n) {
            var i, r, o = e.nodeType;
            if (3 !== o && 8 !== o && 2 !== o) return 1 === o && oe.isXMLDoc(e) || (t = oe.propFix[t] || t, r = oe.propHooks[t]), void 0 !== n ? r && "set" in r && void 0 !== (i = r.set(e, n, t)) ? i : e[t] = n : r && "get" in r && null !== (i = r.get(e, t)) ? i : e[t]
        },
        propHooks: {
            tabIndex: {
                get: function(e) {
                    var t = oe.find.attr(e, "tabindex");
                    return t ? parseInt(t, 10) : ct.test(e.nodeName) || ft.test(e.nodeName) && e.href ? 0 : -1
                }
            }
        },
        propFix: {
            "for": "htmlFor",
            "class": "className"
        }
    }), ie.optSelected || (oe.propHooks.selected = {
        get: function(e) {
            var t = e.parentNode;
            return t && t.parentNode && t.parentNode.selectedIndex, null
        },
        set: function(e) {
            var t = e.parentNode;
            t && (t.selectedIndex, t.parentNode && t.parentNode.selectedIndex)
        }
    }), oe.each(["tabIndex", "readOnly", "maxLength", "cellSpacing", "cellPadding", "rowSpan", "colSpan", "useMap", "frameBorder", "contentEditable"], function() {
        oe.propFix[this.toLowerCase()] = this
    });
    var dt = /[\t\r\n\f]/g;
    oe.fn.extend({
        addClass: function(e) {
            var t, n, i, r, o, s, a, u = 0;
            if (oe.isFunction(e)) return this.each(function(t) {
                oe(this).addClass(e.call(this, t, I(this)))
            });
            if ("string" == typeof e && e)
                for (t = e.match(be) || []; n = this[u++];)
                    if (r = I(n), i = 1 === n.nodeType && (" " + r + " ").replace(dt, " ")) {
                        for (s = 0; o = t[s++];) i.indexOf(" " + o + " ") < 0 && (i += o + " ");
                        a = oe.trim(i), r !== a && n.setAttribute("class", a)
                    }
            return this
        },
        removeClass: function(e) {
            var t, n, i, r, o, s, a, u = 0;
            if (oe.isFunction(e)) return this.each(function(t) {
                oe(this).removeClass(e.call(this, t, I(this)))
            });
            if (!arguments.length) return this.attr("class", "");
            if ("string" == typeof e && e)
                for (t = e.match(be) || []; n = this[u++];)
                    if (r = I(n), i = 1 === n.nodeType && (" " + r + " ").replace(dt, " ")) {
                        for (s = 0; o = t[s++];)
                            for (; i.indexOf(" " + o + " ") > -1;) i = i.replace(" " + o + " ", " ");
                        a = oe.trim(i), r !== a && n.setAttribute("class", a)
                    }
            return this
        },
        toggleClass: function(e, t) {
            var n = typeof e;
            return "boolean" == typeof t && "string" === n ? t ? this.addClass(e) : this.removeClass(e) : oe.isFunction(e) ? this.each(function(n) {
                oe(this).toggleClass(e.call(this, n, I(this), t), t)
            }) : this.each(function() {
                var t, i, r, o;
                if ("string" === n)
                    for (i = 0, r = oe(this), o = e.match(be) || []; t = o[i++];) r.hasClass(t) ? r.removeClass(t) : r.addClass(t);
                else void 0 !== e && "boolean" !== n || (t = I(this), t && Ee.set(this, "__className__", t), this.setAttribute && this.setAttribute("class", t || e === !1 ? "" : Ee.get(this, "__className__") || ""))
            })
        },
        hasClass: function(e) {
            var t, n, i = 0;
            for (t = " " + e + " "; n = this[i++];)
                if (1 === n.nodeType && (" " + I(n) + " ").replace(dt, " ").indexOf(t) > -1) return !0;
            return !1
        }
    });
    var pt = /\r/g,
        ht = /[\x20\t\r\n\f]+/g;
    oe.fn.extend({
        val: function(e) {
            var t, n, i, r = this[0]; {
                if (arguments.length) return i = oe.isFunction(e), this.each(function(n) {
                    var r;
                    1 === this.nodeType && (r = i ? e.call(this, n, oe(this).val()) : e, null == r ? r = "" : "number" == typeof r ? r += "" : oe.isArray(r) && (r = oe.map(r, function(e) {
                        return null == e ? "" : e + ""
                    })), t = oe.valHooks[this.type] || oe.valHooks[this.nodeName.toLowerCase()], t && "set" in t && void 0 !== t.set(this, r, "value") || (this.value = r))
                });
                if (r) return t = oe.valHooks[r.type] || oe.valHooks[r.nodeName.toLowerCase()], t && "get" in t && void 0 !== (n = t.get(r, "value")) ? n : (n = r.value, "string" == typeof n ? n.replace(pt, "") : null == n ? "" : n)
            }
        }
    }), oe.extend({
        valHooks: {
            option: {
                get: function(e) {
                    var t = oe.find.attr(e, "value");
                    return null != t ? t : oe.trim(oe.text(e)).replace(ht, " ")
                }
            },
            select: {
                get: function(e) {
                    for (var t, n, i = e.options, r = e.selectedIndex, o = "select-one" === e.type || 0 > r, s = o ? null : [], a = o ? r + 1 : i.length, u = 0 > r ? a : o ? r : 0; a > u; u++)
                        if (n = i[u], (n.selected || u === r) && (ie.optDisabled ? !n.disabled : null === n.getAttribute("disabled")) && (!n.parentNode.disabled || !oe.nodeName(n.parentNode, "optgroup"))) {
                            if (t = oe(n).val(), o) return t;
                            s.push(t)
                        }
                    return s
                },
                set: function(e, t) {
                    for (var n, i, r = e.options, o = oe.makeArray(t), s = r.length; s--;) i = r[s], (i.selected = oe.inArray(oe.valHooks.option.get(i), o) > -1) && (n = !0);
                    return n || (e.selectedIndex = -1), o
                }
            }
        }
    }), oe.each(["radio", "checkbox"], function() {
        oe.valHooks[this] = {
            set: function(e, t) {
                return oe.isArray(t) ? e.checked = oe.inArray(oe(e).val(), t) > -1 : void 0
            }
        }, ie.checkOn || (oe.valHooks[this].get = function(e) {
            return null === e.getAttribute("value") ? "on" : e.value
        })
    });
    var gt = /^(?:focusinfocus|focusoutblur)$/;
    oe.extend(oe.event, {
        trigger: function(t, n, i, r) {
            var o, s, a, u, l, c, f, d = [i || J],
                p = ne.call(t, "type") ? t.type : t,
                h = ne.call(t, "namespace") ? t.namespace.split(".") : [];
            if (s = a = i = i || J, 3 !== i.nodeType && 8 !== i.nodeType && !gt.test(p + oe.event.triggered) && (p.indexOf(".") > -1 && (h = p.split("."), p = h.shift(), h.sort()), l = p.indexOf(":") < 0 && "on" + p, t = t[oe.expando] ? t : new oe.Event(p, "object" == typeof t && t), t.isTrigger = r ? 2 : 3, t.namespace = h.join("."), t.rnamespace = t.namespace ? new RegExp("(^|\\.)" + h.join("\\.(?:.*\\.|)") + "(\\.|$)") : null, t.result = void 0, t.target || (t.target = i), n = null == n ? [t] : oe.makeArray(n, [t]), f = oe.event.special[p] || {}, r || !f.trigger || f.trigger.apply(i, n) !== !1)) {
                if (!r && !f.noBubble && !oe.isWindow(i)) {
                    for (u = f.delegateType || p, gt.test(u + p) || (s = s.parentNode); s; s = s.parentNode) d.push(s), a = s;
                    a === (i.ownerDocument || J) && d.push(a.defaultView || a.parentWindow || e)
                }
                for (o = 0;
                    (s = d[o++]) && !t.isPropagationStopped();) t.type = o > 1 ? u : f.bindType || p, c = (Ee.get(s, "events") || {})[t.type] && Ee.get(s, "handle"), c && c.apply(s, n), c = l && s[l], c && c.apply && ke(s) && (t.result = c.apply(s, n), t.result === !1 && t.preventDefault());
                return t.type = p, r || t.isDefaultPrevented() || f._default && f._default.apply(d.pop(), n) !== !1 || !ke(i) || l && oe.isFunction(i[p]) && !oe.isWindow(i) && (a = i[l], a && (i[l] = null), oe.event.triggered = p, i[p](), oe.event.triggered = void 0, a && (i[l] = a)), t.result
            }
        },
        simulate: function(e, t, n) {
            var i = oe.extend(new oe.Event, n, {
                type: e,
                isSimulated: !0
            });
            oe.event.trigger(i, null, t)
        }
    }), oe.fn.extend({
        trigger: function(e, t) {
            return this.each(function() {
                oe.event.trigger(e, t, this)
            })
        },
        triggerHandler: function(e, t) {
            var n = this[0];
            return n ? oe.event.trigger(e, t, n, !0) : void 0
        }
    }), oe.each("blur focus focusin focusout load resize scroll unload click dblclick mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave change select submit keydown keypress keyup error contextmenu".split(" "), function(e, t) {
        oe.fn[t] = function(e, n) {
            return arguments.length > 0 ? this.on(t, null, e, n) : this.trigger(t)
        }
    }), oe.fn.extend({
        hover: function(e, t) {
            return this.mouseenter(e).mouseleave(t || e)
        }
    }), ie.focusin = "onfocusin" in e, ie.focusin || oe.each({
        focus: "focusin",
        blur: "focusout"
    }, function(e, t) {
        var n = function(e) {
            oe.event.simulate(t, e.target, oe.event.fix(e))
        };
        oe.event.special[t] = {
            setup: function() {
                var i = this.ownerDocument || this,
                    r = Ee.access(i, t);
                r || i.addEventListener(e, n, !0), Ee.access(i, t, (r || 0) + 1)
            },
            teardown: function() {
                var i = this.ownerDocument || this,
                    r = Ee.access(i, t) - 1;
                r ? Ee.access(i, t, r) : (i.removeEventListener(e, n, !0), Ee.remove(i, t))
            }
        }
    });
    var mt = e.location,
        vt = oe.now(),
        yt = /\?/;
    oe.parseJSON = function(e) {
        return JSON.parse(e + "")
    }, oe.parseXML = function(t) {
        var n;
        if (!t || "string" != typeof t) return null;
        try {
            n = (new e.DOMParser).parseFromString(t, "text/xml")
        } catch (i) {
            n = void 0
        }
        return n && !n.getElementsByTagName("parsererror").length || oe.error("Invalid XML: " + t), n
    };
    var xt = /#.*$/,
        wt = /([?&])_=[^&]*/,
        bt = /^(.*?):[ \t]*([^\r\n]*)$/gm,
        Tt = /^(?:about|app|app-storage|.+-extension|file|res|widget):$/,
        Ct = /^(?:GET|HEAD)$/,
        kt = /^\/\//,
        Et = {},
        St = {},
        Nt = "*/".concat("*"),
        At = J.createElement("a");
    At.href = mt.href, oe.extend({
        active: 0,
        lastModified: {},
        etag: {},
        ajaxSettings: {
            url: mt.href,
            type: "GET",
            isLocal: Tt.test(mt.protocol),
            global: !0,
            processData: !0,
            async: !0,
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            accepts: {
                "*": Nt,
                text: "text/plain",
                html: "text/html",
                xml: "application/xml, text/xml",
                json: "application/json, text/javascript"
            },
            contents: {
                xml: /\bxml\b/,
                html: /\bhtml/,
                json: /\bjson\b/
            },
            responseFields: {
                xml: "responseXML",
                text: "responseText",
                json: "responseJSON"
            },
            converters: {
                "* text": String,
                "text html": !0,
                "text json": oe.parseJSON,
                "text xml": oe.parseXML
            },
            flatOptions: {
                url: !0,
                context: !0
            }
        },
        ajaxSetup: function(e, t) {
            return t ? _(_(e, oe.ajaxSettings), t) : _(oe.ajaxSettings, e)
        },
        ajaxPrefilter: B(Et),
        ajaxTransport: B(St),
        ajax: function(t, n) {
            function i(t, n, i, a) {
                var l, f, y, x, b, C = n;
                2 !== w && (w = 2, u && e.clearTimeout(u), r = void 0, s = a || "", T.readyState = t > 0 ? 4 : 0, l = t >= 200 && 300 > t || 304 === t, i && (x = z(d, T, i)), x = X(d, x, T, l), l ? (d.ifModified && (b = T.getResponseHeader("Last-Modified"), b && (oe.lastModified[o] = b), b = T.getResponseHeader("etag"), b && (oe.etag[o] = b)), 204 === t || "HEAD" === d.type ? C = "nocontent" : 304 === t ? C = "notmodified" : (C = x.state, f = x.data, y = x.error, l = !y)) : (y = C, !t && C || (C = "error", 0 > t && (t = 0))), T.status = t, T.statusText = (n || C) + "", l ? g.resolveWith(p, [f, C, T]) : g.rejectWith(p, [T, C, y]), T.statusCode(v), v = void 0, c && h.trigger(l ? "ajaxSuccess" : "ajaxError", [T, d, l ? f : y]), m.fireWith(p, [T, C]), c && (h.trigger("ajaxComplete", [T, d]), --oe.active || oe.event.trigger("ajaxStop")))
            }
            "object" == typeof t && (n = t, t = void 0), n = n || {};
            var r, o, s, a, u, l, c, f, d = oe.ajaxSetup({}, n),
                p = d.context || d,
                h = d.context && (p.nodeType || p.jquery) ? oe(p) : oe.event,
                g = oe.Deferred(),
                m = oe.Callbacks("once memory"),
                v = d.statusCode || {},
                y = {},
                x = {},
                w = 0,
                b = "canceled",
                T = {
                    readyState: 0,
                    getResponseHeader: function(e) {
                        var t;
                        if (2 === w) {
                            if (!a)
                                for (a = {}; t = bt.exec(s);) a[t[1].toLowerCase()] = t[2];
                            t = a[e.toLowerCase()]
                        }
                        return null == t ? null : t
                    },
                    getAllResponseHeaders: function() {
                        return 2 === w ? s : null
                    },
                    setRequestHeader: function(e, t) {
                        var n = e.toLowerCase();
                        return w || (e = x[n] = x[n] || e, y[e] = t), this
                    },
                    overrideMimeType: function(e) {
                        return w || (d.mimeType = e), this
                    },
                    statusCode: function(e) {
                        var t;
                        if (e)
                            if (2 > w)
                                for (t in e) v[t] = [v[t], e[t]];
                            else T.always(e[T.status]);
                        return this
                    },
                    abort: function(e) {
                        var t = e || b;
                        return r && r.abort(t), i(0, t), this
                    }
                };
            if (g.promise(T).complete = m.add, T.success = T.done, T.error = T.fail, d.url = ((t || d.url || mt.href) + "").replace(xt, "").replace(kt, mt.protocol + "//"), d.type = n.method || n.type || d.method || d.type, d.dataTypes = oe.trim(d.dataType || "*").toLowerCase().match(be) || [""], null == d.crossDomain) {
                l = J.createElement("a");
                try {
                    l.href = d.url, l.href = l.href, d.crossDomain = At.protocol + "//" + At.host != l.protocol + "//" + l.host
                } catch (C) {
                    d.crossDomain = !0
                }
            }
            if (d.data && d.processData && "string" != typeof d.data && (d.data = oe.param(d.data, d.traditional)), R(Et, d, n, T), 2 === w) return T;
            c = oe.event && d.global, c && 0 === oe.active++ && oe.event.trigger("ajaxStart"), d.type = d.type.toUpperCase(), d.hasContent = !Ct.test(d.type), o = d.url, d.hasContent || (d.data && (o = d.url += (yt.test(o) ? "&" : "?") + d.data, delete d.data), d.cache === !1 && (d.url = wt.test(o) ? o.replace(wt, "$1_=" + vt++) : o + (yt.test(o) ? "&" : "?") + "_=" + vt++)), d.ifModified && (oe.lastModified[o] && T.setRequestHeader("If-Modified-Since", oe.lastModified[o]), oe.etag[o] && T.setRequestHeader("If-None-Match", oe.etag[o])), (d.data && d.hasContent && d.contentType !== !1 || n.contentType) && T.setRequestHeader("Content-Type", d.contentType), T.setRequestHeader("Accept", d.dataTypes[0] && d.accepts[d.dataTypes[0]] ? d.accepts[d.dataTypes[0]] + ("*" !== d.dataTypes[0] ? ", " + Nt + "; q=0.01" : "") : d.accepts["*"]);
            for (f in d.headers) T.setRequestHeader(f, d.headers[f]);
            if (d.beforeSend && (d.beforeSend.call(p, T, d) === !1 || 2 === w)) return T.abort();
            b = "abort";
            for (f in {
                    success: 1,
                    error: 1,
                    complete: 1
                }) T[f](d[f]);
            if (r = R(St, d, n, T)) {
                if (T.readyState = 1, c && h.trigger("ajaxSend", [T, d]), 2 === w) return T;
                d.async && d.timeout > 0 && (u = e.setTimeout(function() {
                    T.abort("timeout")
                }, d.timeout));
                try {
                    w = 1, r.send(y, i)
                } catch (C) {
                    if (!(2 > w)) throw C;
                    i(-1, C)
                }
            } else i(-1, "No Transport");
            return T
        },
        getJSON: function(e, t, n) {
            return oe.get(e, t, n, "json")
        },
        getScript: function(e, t) {
            return oe.get(e, void 0, t, "script")
        }
    }), oe.each(["get", "post"], function(e, t) {
        oe[t] = function(e, n, i, r) {
            return oe.isFunction(n) && (r = r || i, i = n, n = void 0), oe.ajax(oe.extend({
                url: e,
                type: t,
                dataType: r,
                data: n,
                success: i
            }, oe.isPlainObject(e) && e))
        }
    }), oe._evalUrl = function(e) {
        return oe.ajax({
            url: e,
            type: "GET",
            dataType: "script",
            async: !1,
            global: !1,
            "throws": !0
        })
    }, oe.fn.extend({
        wrapAll: function(e) {
            var t;
            return oe.isFunction(e) ? this.each(function(t) {
                oe(this).wrapAll(e.call(this, t))
            }) : (this[0] && (t = oe(e, this[0].ownerDocument).eq(0).clone(!0), this[0].parentNode && t.insertBefore(this[0]), t.map(function() {
                for (var e = this; e.firstElementChild;) e = e.firstElementChild;
                return e
            }).append(this)), this)
        },
        wrapInner: function(e) {
            return oe.isFunction(e) ? this.each(function(t) {
                oe(this).wrapInner(e.call(this, t))
            }) : this.each(function() {
                var t = oe(this),
                    n = t.contents();
                n.length ? n.wrapAll(e) : t.append(e)
            })
        },
        wrap: function(e) {
            var t = oe.isFunction(e);
            return this.each(function(n) {
                oe(this).wrapAll(t ? e.call(this, n) : e)
            })
        },
        unwrap: function() {
            return this.parent().each(function() {
                oe.nodeName(this, "body") || oe(this).replaceWith(this.childNodes)
            }).end()
        }
    }), oe.expr.filters.hidden = function(e) {
        return !oe.expr.filters.visible(e)
    }, oe.expr.filters.visible = function(e) {
        return e.offsetWidth > 0 || e.offsetHeight > 0 || e.getClientRects().length > 0
    };
    var jt = /%20/g,
        Dt = /\[\]$/,
        Lt = /\r?\n/g,
        Ot = /^(?:submit|button|image|reset|file)$/i,
        qt = /^(?:input|select|textarea|keygen)/i;
    oe.param = function(e, t) {
        var n, i = [],
            r = function(e, t) {
                t = oe.isFunction(t) ? t() : null == t ? "" : t, i[i.length] = encodeURIComponent(e) + "=" + encodeURIComponent(t)
            };
        if (void 0 === t && (t = oe.ajaxSettings && oe.ajaxSettings.traditional), oe.isArray(e) || e.jquery && !oe.isPlainObject(e)) oe.each(e, function() {
            r(this.name, this.value)
        });
        else
            for (n in e) U(n, e[n], t, r);
        return i.join("&").replace(jt, "+")
    }, oe.fn.extend({
        serialize: function() {
            return oe.param(this.serializeArray())
        },
        serializeArray: function() {
            return this.map(function() {
                var e = oe.prop(this, "elements");
                return e ? oe.makeArray(e) : this
            }).filter(function() {
                var e = this.type;
                return this.name && !oe(this).is(":disabled") && qt.test(this.nodeName) && !Ot.test(e) && (this.checked || !qe.test(e))
            }).map(function(e, t) {
                var n = oe(this).val();
                return null == n ? null : oe.isArray(n) ? oe.map(n, function(e) {
                    return {
                        name: t.name,
                        value: e.replace(Lt, "\r\n")
                    }
                }) : {
                    name: t.name,
                    value: n.replace(Lt, "\r\n")
                }
            }).get()
        }
    }), oe.ajaxSettings.xhr = function() {
        try {
            return new e.XMLHttpRequest
        } catch (t) {}
    };
    var Ft = {
            0: 200,
            1223: 204
        },
        $t = oe.ajaxSettings.xhr();
    ie.cors = !!$t && "withCredentials" in $t,
        ie.ajax = $t = !!$t, oe.ajaxTransport(function(t) {
            var n, i;
            return ie.cors || $t && !t.crossDomain ? {
                send: function(r, o) {
                    var s, a = t.xhr();
                    if (a.open(t.type, t.url, t.async, t.username, t.password), t.xhrFields)
                        for (s in t.xhrFields) a[s] = t.xhrFields[s];
                    t.mimeType && a.overrideMimeType && a.overrideMimeType(t.mimeType), t.crossDomain || r["X-Requested-With"] || (r["X-Requested-With"] = "XMLHttpRequest");
                    for (s in r) a.setRequestHeader(s, r[s]);
                    n = function(e) {
                        return function() {
                            n && (n = i = a.onload = a.onerror = a.onabort = a.onreadystatechange = null, "abort" === e ? a.abort() : "error" === e ? "number" != typeof a.status ? o(0, "error") : o(a.status, a.statusText) : o(Ft[a.status] || a.status, a.statusText, "text" !== (a.responseType || "text") || "string" != typeof a.responseText ? {
                                binary: a.response
                            } : {
                                text: a.responseText
                            }, a.getAllResponseHeaders()))
                        }
                    }, a.onload = n(), i = a.onerror = n("error"), void 0 !== a.onabort ? a.onabort = i : a.onreadystatechange = function() {
                        4 === a.readyState && e.setTimeout(function() {
                            n && i()
                        })
                    }, n = n("abort");
                    try {
                        a.send(t.hasContent && t.data || null)
                    } catch (u) {
                        if (n) throw u
                    }
                },
                abort: function() {
                    n && n()
                }
            } : void 0
        }), oe.ajaxSetup({
            accepts: {
                script: "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript"
            },
            contents: {
                script: /\b(?:java|ecma)script\b/
            },
            converters: {
                "text script": function(e) {
                    return oe.globalEval(e), e
                }
            }
        }), oe.ajaxPrefilter("script", function(e) {
            void 0 === e.cache && (e.cache = !1), e.crossDomain && (e.type = "GET")
        }), oe.ajaxTransport("script", function(e) {
            if (e.crossDomain) {
                var t, n;
                return {
                    send: function(i, r) {
                        t = oe("<script>").prop({
                            charset: e.scriptCharset,
                            src: e.url
                        }).on("load error", n = function(e) {
                            t.remove(), n = null, e && r("error" === e.type ? 404 : 200, e.type)
                        }), J.head.appendChild(t[0])
                    },
                    abort: function() {
                        n && n()
                    }
                }
            }
        });
    var Wt = [],
        Ht = /(=)\?(?=&|$)|\?\?/;
    oe.ajaxSetup({
        jsonp: "callback",
        jsonpCallback: function() {
            var e = Wt.pop() || oe.expando + "_" + vt++;
            return this[e] = !0, e
        }
    }), oe.ajaxPrefilter("json jsonp", function(t, n, i) {
        var r, o, s, a = t.jsonp !== !1 && (Ht.test(t.url) ? "url" : "string" == typeof t.data && 0 === (t.contentType || "").indexOf("application/x-www-form-urlencoded") && Ht.test(t.data) && "data");
        return a || "jsonp" === t.dataTypes[0] ? (r = t.jsonpCallback = oe.isFunction(t.jsonpCallback) ? t.jsonpCallback() : t.jsonpCallback, a ? t[a] = t[a].replace(Ht, "$1" + r) : t.jsonp !== !1 && (t.url += (yt.test(t.url) ? "&" : "?") + t.jsonp + "=" + r), t.converters["script json"] = function() {
            return s || oe.error(r + " was not called"), s[0]
        }, t.dataTypes[0] = "json", o = e[r], e[r] = function() {
            s = arguments
        }, i.always(function() {
            void 0 === o ? oe(e).removeProp(r) : e[r] = o, t[r] && (t.jsonpCallback = n.jsonpCallback, Wt.push(r)), s && oe.isFunction(o) && o(s[0]), s = o = void 0
        }), "script") : void 0
    }), oe.parseHTML = function(e, t, n) {
        if (!e || "string" != typeof e) return null;
        "boolean" == typeof t && (n = t, t = !1), t = t || J;
        var i = he.exec(e),
            r = !n && [];
        return i ? [t.createElement(i[1])] : (i = d([e], t, r), r && r.length && oe(r).remove(), oe.merge([], i.childNodes))
    };
    var Pt = oe.fn.load;
    oe.fn.load = function(e, t, n) {
        if ("string" != typeof e && Pt) return Pt.apply(this, arguments);
        var i, r, o, s = this,
            a = e.indexOf(" ");
        return a > -1 && (i = oe.trim(e.slice(a)), e = e.slice(0, a)), oe.isFunction(t) ? (n = t, t = void 0) : t && "object" == typeof t && (r = "POST"), s.length > 0 && oe.ajax({
            url: e,
            type: r || "GET",
            dataType: "html",
            data: t
        }).done(function(e) {
            o = arguments, s.html(i ? oe("<div>").append(oe.parseHTML(e)).find(i) : e)
        }).always(n && function(e, t) {
            s.each(function() {
                n.apply(this, o || [e.responseText, t, e])
            })
        }), this
    }, oe.each(["ajaxStart", "ajaxStop", "ajaxComplete", "ajaxError", "ajaxSuccess", "ajaxSend"], function(e, t) {
        oe.fn[t] = function(e) {
            return this.on(t, e)
        }
    }), oe.expr.filters.animated = function(e) {
        return oe.grep(oe.timers, function(t) {
            return e === t.elem
        }).length
    }, oe.offset = {
        setOffset: function(e, t, n) {
            var i, r, o, s, a, u, l, c = oe.css(e, "position"),
                f = oe(e),
                d = {};
            "static" === c && (e.style.position = "relative"), a = f.offset(), o = oe.css(e, "top"), u = oe.css(e, "left"), l = ("absolute" === c || "fixed" === c) && (o + u).indexOf("auto") > -1, l ? (i = f.position(), s = i.top, r = i.left) : (s = parseFloat(o) || 0, r = parseFloat(u) || 0), oe.isFunction(t) && (t = t.call(e, n, oe.extend({}, a))), null != t.top && (d.top = t.top - a.top + s), null != t.left && (d.left = t.left - a.left + r), "using" in t ? t.using.call(e, d) : f.css(d)
        }
    }, oe.fn.extend({
        offset: function(e) {
            if (arguments.length) return void 0 === e ? this : this.each(function(t) {
                oe.offset.setOffset(this, e, t)
            });
            var t, n, i = this[0],
                r = {
                    top: 0,
                    left: 0
                },
                o = i && i.ownerDocument;
            if (o) return t = o.documentElement, oe.contains(t, i) ? (r = i.getBoundingClientRect(), n = V(o), {
                top: r.top + n.pageYOffset - t.clientTop,
                left: r.left + n.pageXOffset - t.clientLeft
            }) : r
        },
        position: function() {
            if (this[0]) {
                var e, t, n = this[0],
                    i = {
                        top: 0,
                        left: 0
                    };
                return "fixed" === oe.css(n, "position") ? t = n.getBoundingClientRect() : (e = this.offsetParent(), t = this.offset(), oe.nodeName(e[0], "html") || (i = e.offset()), i.top += oe.css(e[0], "borderTopWidth", !0), i.left += oe.css(e[0], "borderLeftWidth", !0)), {
                    top: t.top - i.top - oe.css(n, "marginTop", !0),
                    left: t.left - i.left - oe.css(n, "marginLeft", !0)
                }
            }
        },
        offsetParent: function() {
            return this.map(function() {
                for (var e = this.offsetParent; e && "static" === oe.css(e, "position");) e = e.offsetParent;
                return e || Qe
            })
        }
    }), oe.each({
        scrollLeft: "pageXOffset",
        scrollTop: "pageYOffset"
    }, function(e, t) {
        var n = "pageYOffset" === t;
        oe.fn[e] = function(i) {
            return Ce(this, function(e, i, r) {
                var o = V(e);
                return void 0 === r ? o ? o[t] : e[i] : void(o ? o.scrollTo(n ? o.pageXOffset : r, n ? r : o.pageYOffset) : e[i] = r)
            }, e, i, arguments.length)
        }
    }), oe.each(["top", "left"], function(e, t) {
        oe.cssHooks[t] = N(ie.pixelPosition, function(e, n) {
            return n ? (n = S(e, t), Je.test(n) ? oe(e).position()[t] + "px" : n) : void 0
        })
    }), oe.each({
        Height: "height",
        Width: "width"
    }, function(e, t) {
        oe.each({
            padding: "inner" + e,
            content: t,
            "": "outer" + e
        }, function(n, i) {
            oe.fn[i] = function(i, r) {
                var o = arguments.length && (n || "boolean" != typeof i),
                    s = n || (i === !0 || r === !0 ? "margin" : "border");
                return Ce(this, function(t, n, i) {
                    var r;
                    return oe.isWindow(t) ? t.document.documentElement["client" + e] : 9 === t.nodeType ? (r = t.documentElement, Math.max(t.body["scroll" + e], r["scroll" + e], t.body["offset" + e], r["offset" + e], r["client" + e])) : void 0 === i ? oe.css(t, n, s) : oe.style(t, n, i, s)
                }, t, o ? i : void 0, o, null)
            }
        })
    }), oe.fn.extend({
        bind: function(e, t, n) {
            return this.on(e, null, t, n)
        },
        unbind: function(e, t) {
            return this.off(e, null, t)
        },
        delegate: function(e, t, n, i) {
            return this.on(t, e, n, i)
        },
        undelegate: function(e, t, n) {
            return 1 === arguments.length ? this.off(e, "**") : this.off(t, e || "**", n)
        },
        size: function() {
            return this.length
        }
    }), oe.fn.andSelf = oe.fn.addBack, "function" == typeof define && define.amd && define("jquery", [], function() {
        return oe
    });
    var Mt = e.jQuery,
        It = e.$;
    return oe.noConflict = function(t) {
        return e.$ === oe && (e.$ = It), t && e.jQuery === oe && (e.jQuery = Mt), oe
    }, t || (e.jQuery = e.$ = oe), oe
}),
function(e) {
    "function" == typeof define && define.amd ? define(["jquery"], e) : "object" == typeof module && module.exports ? module.exports = e(require("jquery")) : e(jQuery)
}(function(e) {
    var t = Array.prototype.slice,
        n = Array.prototype.splice,
        i = {
            topSpacing: 0,
            bottomSpacing: 0,
            className: "is-sticky",
            wrapperClassName: "sticky-wrapper",
            center: !1,
            getWidthFrom: "",
            widthFromWrapper: !0,
            responsiveWidth: !1,
            zIndex: "auto"
        },
        r = e(window),
        o = e(document),
        s = [],
        a = r.height(),
        u = function() {
            for (var t = r.scrollTop(), n = o.height(), i = n - a, u = t > i ? i - t : 0, l = 0, c = s.length; c > l; l++) {
                var f = s[l],
                    d = f.stickyWrapper.offset().top,
                    p = d - f.topSpacing - u;
                if (f.stickyWrapper.css("height", f.stickyElement.outerHeight()), p >= t) null !== f.currentTop && (f.stickyElement.css({
                    width: "",
                    position: "",
                    top: "",
                    "z-index": ""
                }), f.stickyElement.parent().removeClass(f.className), f.stickyElement.trigger("sticky-end", [f]), f.currentTop = null);
                else {
                    var h = n - f.stickyElement.outerHeight() - f.topSpacing - f.bottomSpacing - t - u;
                    if (0 > h ? h += f.topSpacing : h = f.topSpacing, f.currentTop !== h) {
                        var g;
                        f.getWidthFrom ? g = e(f.getWidthFrom).width() || null : f.widthFromWrapper && (g = f.stickyWrapper.width()), null == g && (g = f.stickyElement.width()), f.stickyElement.css("width", g).css("position", "fixed").css("top", h).css("z-index", f.zIndex), f.stickyElement.parent().addClass(f.className), null === f.currentTop ? f.stickyElement.trigger("sticky-start", [f]) : f.stickyElement.trigger("sticky-update", [f]), f.currentTop === f.topSpacing && f.currentTop > h || null === f.currentTop && h < f.topSpacing ? f.stickyElement.trigger("sticky-bottom-reached", [f]) : null !== f.currentTop && h === f.topSpacing && f.currentTop < h && f.stickyElement.trigger("sticky-bottom-unreached", [f]), f.currentTop = h
                    }
                    var m = f.stickyWrapper.parent(),
                        v = f.stickyElement.offset().top + f.stickyElement.outerHeight() >= m.offset().top + m.outerHeight() && f.stickyElement.offset().top <= f.topSpacing;
                    v ? f.stickyElement.css("position", "absolute").css("top", "").css("bottom", 0).css("z-index", "") : f.stickyElement.css("position", "fixed").css("top", h).css("bottom", "").css("z-index", f.zIndex)
                }
            }
        },
        l = function() {
            a = r.height();
            for (var t = 0, n = s.length; n > t; t++) {
                var i = s[t],
                    o = null;
                i.getWidthFrom ? i.responsiveWidth && (o = e(i.getWidthFrom).width()) : i.widthFromWrapper && (o = i.stickyWrapper.width()), null != o && i.stickyElement.css("width", o)
            }
        },
        c = {
            init: function(t) {
                return this.each(function() {
                    var n = e.extend({}, i, t),
                        r = e(this),
                        o = r.attr("id"),
                        a = o ? o + "-" + i.wrapperClassName : i.wrapperClassName,
                        u = e("<div></div>").attr("id", a).addClass(n.wrapperClassName);
                    r.wrapAll(function() {
                        return 0 == e(this).parent("#" + a).length ? u : void 0
                    });
                    var l = r.parent();
                    n.center && l.css({
                        width: r.outerWidth(),
                        marginLeft: "auto",
                        marginRight: "auto"
                    }), "right" === r.css("float") && r.css({
                        "float": "none"
                    }).parent().css({
                        "float": "right"
                    }), n.stickyElement = r, n.stickyWrapper = l, n.currentTop = null, s.push(n), c.setWrapperHeight(this), c.setupChangeListeners(this)
                })
            },
            setWrapperHeight: function(t) {
                var n = e(t),
                    i = n.parent();
                i && i.css("height", n.outerHeight())
            },
            setupChangeListeners: function(e) {
                if (window.MutationObserver) {
                    var t = new window.MutationObserver(function(t) {
                        (t[0].addedNodes.length || t[0].removedNodes.length) && c.setWrapperHeight(e)
                    });
                    t.observe(e, {
                        subtree: !0,
                        childList: !0
                    })
                } else window.addEventListener ? (e.addEventListener("DOMNodeInserted", function() {
                    c.setWrapperHeight(e)
                }, !1), e.addEventListener("DOMNodeRemoved", function() {
                    c.setWrapperHeight(e)
                }, !1)) : window.attachEvent && (e.attachEvent("onDOMNodeInserted", function() {
                    c.setWrapperHeight(e)
                }), e.attachEvent("onDOMNodeRemoved", function() {
                    c.setWrapperHeight(e)
                }))
            },
            update: u,
            unstick: function(t) {
                return this.each(function() {
                    for (var t = this, i = e(t), r = -1, o = s.length; o-- > 0;) s[o].stickyElement.get(0) === t && (n.call(s, o, 1), r = o); - 1 !== r && (i.unwrap(), i.css({
                        width: "",
                        position: "",
                        top: "",
                        "float": "",
                        "z-index": ""
                    }))
                })
            }
        };
    window.addEventListener ? (window.addEventListener("scroll", u, !1), window.addEventListener("resize", l, !1)) : window.attachEvent && (window.attachEvent("onscroll", u), window.attachEvent("onresize", l)), e.fn.sticky = function(n) {
        return c[n] ? c[n].apply(this, t.call(arguments, 1)) : "object" != typeof n && n ? void e.error("Method " + n + " does not exist on jQuery.sticky") : c.init.apply(this, arguments)
    }, e.fn.unstick = function(n) {
        return c[n] ? c[n].apply(this, t.call(arguments, 1)) : "object" != typeof n && n ? void e.error("Method " + n + " does not exist on jQuery.sticky") : c.unstick.apply(this, arguments)
    }, e(function() {
        setTimeout(u, 0)
    })
}),
function(e) {
    "use strict";
    "function" == typeof define && define.amd ? define(["jquery"], e) : "undefined" != typeof module && module.exports ? module.exports = e(require("jquery")) : e(jQuery)
}(function(e) {
    "use strict";

    function t(t) {
        return !t.nodeName || -1 !== e.inArray(t.nodeName.toLowerCase(), ["iframe", "#document", "html", "body"])
    }

    function n(t) {
        return e.isFunction(t) || e.isPlainObject(t) ? t : {
            top: t,
            left: t
        }
    }
    var i = e.scrollTo = function(t, n, i) {
        return e(window).scrollTo(t, n, i)
    };
    return i.defaults = {
        axis: "xy",
        duration: 0,
        limit: !0
    }, e.fn.scrollTo = function(r, o, s) {
        "object" == typeof o && (s = o, o = 0), "function" == typeof s && (s = {
            onAfter: s
        }), "max" === r && (r = 9e9), s = e.extend({}, i.defaults, s), o = o || s.duration;
        var a = s.queue && s.axis.length > 1;
        return a && (o /= 2), s.offset = n(s.offset), s.over = n(s.over), this.each(function() {
            function u(t) {
                var n = e.extend({}, s, {
                    queue: !0,
                    duration: o,
                    complete: t && function() {
                        t.call(f, p, s)
                    }
                });
                d.animate(h, n)
            }
            if (null !== r) {
                var l, c = t(this),
                    f = c ? this.contentWindow || window : this,
                    d = e(f),
                    p = r,
                    h = {};
                switch (typeof p) {
                    case "number":
                    case "string":
                        if (/^([+-]=?)?\d+(\.\d+)?(px|%)?$/.test(p)) {
                            p = n(p);
                            break
                        }
                        p = c ? e(p) : e(p, f);
                    case "object":
                        if (0 === p.length) return;
                        (p.is || p.style) && (l = (p = e(p)).offset())
                }
                var g = e.isFunction(s.offset) && s.offset(f, p) || s.offset;
                e.each(s.axis.split(""), function(e, t) {
                    var n = "x" === t ? "Left" : "Top",
                        r = n.toLowerCase(),
                        o = "scroll" + n,
                        m = d[o](),
                        v = i.max(f, t);
                    if (l) h[o] = l[r] + (c ? 0 : m - d.offset()[r]), s.margin && (h[o] -= parseInt(p.css("margin" + n), 10) || 0, h[o] -= parseInt(p.css("border" + n + "Width"), 10) || 0), h[o] += g[r] || 0, s.over[r] && (h[o] += p["x" === t ? "width" : "height"]() * s.over[r]);
                    else {
                        var y = p[r];
                        h[o] = y.slice && "%" === y.slice(-1) ? parseFloat(y) / 100 * v : y
                    }
                    s.limit && /^\d+$/.test(h[o]) && (h[o] = h[o] <= 0 ? 0 : Math.min(h[o], v)), !e && s.axis.length > 1 && (m === h[o] ? h = {} : a && (u(s.onAfterFirst), h = {}))
                }), u(s.onAfter)
            }
        })
    }, i.max = function(n, i) {
        var r = "x" === i ? "Width" : "Height",
            o = "scroll" + r;
        if (!t(n)) return n[o] - e(n)[r.toLowerCase()]();
        var s = "client" + r,
            a = n.ownerDocument || n.document,
            u = a.documentElement,
            l = a.body;
        return Math.max(u[o], l[o]) - Math.min(u[s], l[s])
    }, e.Tween.propHooks.scrollLeft = e.Tween.propHooks.scrollTop = {
        get: function(t) {
            return e(t.elem)[t.prop]()
        },
        set: function(t) {
            var n = this.get(t);
            if (t.options.interrupt && t._last && t._last !== n) return e(t.elem).stop();
            var i = Math.round(t.now);
            n !== i && (e(t.elem)[t.prop](i), t._last = this.get(t))
        }
    }, i
}), 
    function() {
        function e(e, t) {
            document.addEventListener ? e.addEventListener("scroll", t, !1) : e.attachEvent("scroll", t)
        }

        function t(e) {
            document.body ? e() : document.addEventListener ? document.addEventListener("DOMContentLoaded", function t() {
                document.removeEventListener("DOMContentLoaded", t), e()
            }) : document.attachEvent("onreadystatechange", function n() {
                "interactive" != document.readyState && "complete" != document.readyState || (document.detachEvent("onreadystatechange", n), e())
            })
        }

        function n(e) {
            this.a = document.createElement("div"), this.a.setAttribute("aria-hidden", "true"), this.a.appendChild(document.createTextNode(e)), this.b = document.createElement("span"), this.c = document.createElement("span"), this.h = document.createElement("span"), this.f = document.createElement("span"), this.g = -1, this.b.style.cssText = "max-width:none;display:inline-block;position:absolute;height:100%;width:100%;overflow:scroll;font-size:16px;", this.c.style.cssText = "max-width:none;display:inline-block;position:absolute;height:100%;width:100%;overflow:scroll;font-size:16px;", this.f.style.cssText = "max-width:none;display:inline-block;position:absolute;height:100%;width:100%;overflow:scroll;font-size:16px;", this.h.style.cssText = "display:inline-block;width:200%;height:200%;font-size:16px;max-width:none;", this.b.appendChild(this.h), this.c.appendChild(this.f), this.a.appendChild(this.b), this.a.appendChild(this.c)
        }

        function i(e, t) {
            e.a.style.cssText = "max-width:none;min-width:20px;min-height:20px;display:inline-block;overflow:hidden;position:absolute;width:auto;margin:0;padding:0;top:-999px;left:-999px;white-space:nowrap;font:" + t + ";"
        }

        function r(e) {
            var t = e.a.offsetWidth,
                n = t + 100;
            return e.f.style.width = n + "px", e.c.scrollLeft = n, e.b.scrollLeft = e.b.scrollWidth + 100, e.g !== t ? (e.g = t, !0) : !1
        }

        function o(t, n) {
            function i() {
                var e = o;
                r(e) && e.a.parentNode && n(e.g)
            }
            var o = t;
            e(t.b, i), e(t.c, i), r(t)
        }

        function s(e, t) {
            var n = t || {};
            this.family = e, this.style = n.style || "normal", this.weight = n.weight || "normal", this.stretch = n.stretch || "normal"
        }

        function a() {
            if (null === f) {
                var e = document.createElement("div");
                try {
                    e.style.font = "condensed 100px sans-serif"
                } catch (t) {}
                f = "" !== e.style.font
            }
            return f
        }

        function u(e, t) {
            return [e.style, e.weight, a() ? e.stretch : "", "100px", t].join(" ")
        }
        var l = null,
            c = null,
            f = null,
            d = null;
        s.prototype.load = function(e, r) {
            var s = this,
                a = e || "BESbswy",
                f = 0,
                p = r || 3e3,
                h = (new Date).getTime();
            return new Promise(function(e, r) {
                var g;
                if (null === d && (d = !!document.fonts), (g = d) && (null === c && (c = /OS X.*Version\/10\..*Safari/.test(navigator.userAgent) && /Apple/.test(navigator.vendor)), g = !c), g) {
                    g = new Promise(function(e, t) {
                        function n() {
                            (new Date).getTime() - h >= p ? t() : document.fonts.load(u(s, '"' + s.family + '"'), a).then(function(t) {
                                1 <= t.length ? e() : setTimeout(n, 25)
                            }, function() {
                                t()
                            })
                        }
                        n()
                    });
                    var m = new Promise(function(e, t) {
                        f = setTimeout(t, p)
                    });
                    Promise.race([m, g]).then(function() {
                        clearTimeout(f), e(s)
                    }, function() {
                        r(s)
                    })
                } else t(function() {
                    function t() {
                        var t;
                        (t = -1 != v && -1 != y || -1 != v && -1 != x || -1 != y && -1 != x) && ((t = v != y && v != x && y != x) || (null === l && (t = /AppleWebKit\/([0-9]+)(?:\.([0-9]+))/.exec(window.navigator.userAgent), l = !!t && (536 > parseInt(t[1], 10) || 536 === parseInt(t[1], 10) && 11 >= parseInt(t[2], 10))), t = l && (v == w && y == w && x == w || v == b && y == b && x == b || v == T && y == T && x == T)), t = !t), t && (C.parentNode && C.parentNode.removeChild(C), clearTimeout(f), e(s))
                    }

                    function c() {
                        if ((new Date).getTime() - h >= p) C.parentNode && C.parentNode.removeChild(C), r(s);
                        else {
                            var e = document.hidden;
                            !0 !== e && void 0 !== e || (v = d.a.offsetWidth, y = g.a.offsetWidth, x = m.a.offsetWidth, t()), f = setTimeout(c, 50)
                        }
                    }
                    var d = new n(a),
                        g = new n(a),
                        m = new n(a),
                        v = -1,
                        y = -1,
                        x = -1,
                        w = -1,
                        b = -1,
                        T = -1,
                        C = document.createElement("div");
                    C.dir = "ltr", i(d, u(s, "sans-serif")), i(g, u(s, "serif")), i(m, u(s, "monospace")), C.appendChild(d.a), C.appendChild(g.a), C.appendChild(m.a), document.body.appendChild(C), w = d.a.offsetWidth, b = g.a.offsetWidth, T = m.a.offsetWidth, c(), o(d, function(e) {
                        v = e, t()
                    }), i(d, u(s, '"' + s.family + '",sans-serif')), o(g, function(e) {
                        y = e, t()
                    }), i(g, u(s, '"' + s.family + '",serif')), o(m, function(e) {
                        x = e, t()
                    }), i(m, u(s, '"' + s.family + '",monospace'))
                })
            })
        }, "undefined" != typeof module ? module.exports = s : (window.FontFaceObserver = s, window.FontFaceObserver.prototype.load = s.prototype.load)
    }(), console.log("hello, welcome to the simplest start-up starter"), $(".main-cta").sticky({
        responsiveWidth: !0,
        getWidthFrom: ".w150p"
    }), $(".main-cta").on("sticky-start", function() {
        $(".secondary-cta").css("display", "none"), $(".main-cta").css("margin-top", "0.9rem"), $(".main-cta").text("Download"), $("body").toggleClass("is-sticky"), $(".secondary-cta").css("display", "none")
    }), $(".main-cta").on("sticky-end", function() {
        $(".secondary-cta").toggle(), $(".secondary-cta").css("display", ""), $(".main-cta").css("margin-top", ""), $(".main-cta").text("Download on F-Droid"), $("body").toggleClass("is-sticky"), $("body").removeClass("is-expanded"), $(".menu-section-container-nav").css({
            display: "none",
            opacity: 0
        }), $("#nav-toggle").css("display", "block")
    }), $(document).ready(function() {
        $("#nav-toggle").on("click", function() {
            toggleNavigation()
        }), $(".nav-item").on("click", function(e) {
            toggleNavigation(), $.scrollTo("." + $(e.currentTarget).attr("data-target"), {
                duration: 500
            })
        }), $(".footer-nav-item").on("click", function(e) {
            $.scrollTo("." + $(e.currentTarget).attr("data-target"), {
                duration: 500
            })
        }), $(window).resize(function() {
            $("body").hasClass("is-sticky") || $(".main-cta").width("auto")
        })
    });

 WebFont.load({
    google: {
        families: ["Open Sans:400,700,800", "Lora:400,700"]
    }
});
var fontA = new FontFaceObserver("Open Sans", {
        weight: 400
    }),
    fontB = new FontFaceObserver("Open Sans", {
        weight: 700
    }),
    fontC = new FontFaceObserver("Open Sans", {
        weight: 800
    }),
    fontD = new FontFaceObserver("Lora", {
        weight: 400
    }),
    fontE = new FontFaceObserver("Lora", {
        weight: 700
    });
$(document).ready(function() {
    Promise.all([fontA.load(), fontB.load(), fontC.load(), fontD.load(), fontE.load()]).then(function() {
        console.log("Family A & B & C & d & E have loaded"), $("html").addClass("fonts-loaded")
    })
}), $(document).ready(function() {
    $(".plan-a").attr("href", "https://www.payfacile.com/simpleststartupstart/s/plan-a"), $(".plan-b").attr("href", "https://www.payfacile.com/simpleststartupstart/s/plan-b"), $(".plan-c").attr("href", "https://www.payfacile.com/simpleststartupstart/s/plan-c")
});